package hu.fono.test.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ImageTest {

    @Test
    public void testGenerateImage() throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
            document.open();

            BufferedImage bi = ImageIO.read(getClass().getResourceAsStream("/test.png"));

            try (ByteArrayOutputStream imageStream = new ByteArrayOutputStream()) {
                ImageIO.write(bi, "png", imageStream);
                com.lowagie.text.Image pdfImage = com.lowagie.text.Image.getInstance(imageStream.toByteArray());

                PdfContentByte directContent = pdfWriter.getDirectContentUnder();
                float innerWidth = 400;
                float innerHeight = 400;

                PdfTemplate pdfTemplate = directContent.createTemplate(innerWidth, innerHeight);
                pdfTemplate.addImage(pdfImage, innerWidth, 0, 0, innerHeight, 0, 0);
                directContent.addTemplate(pdfTemplate, 1, 0, 0, 1, 10, innerHeight);
            }

            document.close();
            byte[] pdfBytes = baos.toByteArray();

            try (FileOutputStream fos = new FileOutputStream(new File("target/result.pdf"))) {
                fos.write(pdfBytes);
            }
        }
    }

}
