package hu.fono.test.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TextTest {

    @Test
    public void testGenerateText() throws Exception {
        byte[] pdfBytes = createPdf("Test text");

        Assert.assertTrue("PDF size is 0", pdfBytes.length > 0);

        String pdfFileName = "target/textTest.pdf";
        try (FileOutputStream fos = new FileOutputStream(new File(pdfFileName))) {
            fos.write(pdfBytes);
        }

        checkPdfPixels(pdfFileName);
    }

    private byte[] createPdf(String text) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(new Rectangle(0, 0, 200, 100));
            PdfWriter.getInstance(document, baos);
            document.open();

            String fontFileName = "roboto-regular.ttf";
            try (InputStream resourceAsStream = getClass().getResourceAsStream("/" + fontFileName)) {
                byte[] ttfBytes = IOUtils.toByteArray(resourceAsStream);
                BaseFont baseFont = BaseFont.createFont(fontFileName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, ttfBytes, null);
                Font font = new Font(baseFont, 10f, 0, Color.BLACK);

                document.add(new Paragraph(text, font));
            }

            document.close();
            return baos.toByteArray();
        }
    }

    private void checkPdfPixels(String pdfFileName) throws IOException {
        File generatedPdf = new File(pdfFileName);

        try (PDDocument document = PDDocument.load(generatedPdf)) {
            PDFRenderer renderer = new PDFRenderer(document);
            PDPageTree pageTree = document.getPages();
            for (int pageNumber = 1; pageNumber <= pageTree.getCount(); pageNumber++) {
                BufferedImage image = renderer.renderImageWithDPI(pageNumber - 1, 144);

                int width = image.getWidth();
                int height = image.getHeight();

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int rgb = image.getRGB(x, y);
                        if (rgb != -1) {
                            return;
                        }
                    }
                }
            }
        }

        Assert.fail("No text printed");
    }
}
