/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.util;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 *
 * @author TS
 */
public class PdfUtil {

    public static String generatePdfFromHTML(String html, String destination, String wkhtmltopdf) throws IOException, InterruptedException {
        String file = PasswordUtil.generateOpaque("temp") + ".pdf";
        Pdf pdf = new Pdf(new WrapperConfig(wkhtmltopdf));
        pdf.addPageFromString(html);
        pdf.saveAs(destination + file);
        pdf.cleanAllTempFiles();
        return file;
    }

    public static PDPage getImagePage(String url, PDDocument document) throws IOException {
        PDPage page = new PDPage();
        PDImageXObject pdImage = PDImageXObject.createFromFile(url, document);
        PDPageContentStream contents = new PDPageContentStream(document, page);

        //600*770 pixel size image required to adapt full screen
        contents.drawImage(pdImage, 10, 10);
        contents.close();
        return page;
    }

    public static Boolean isBlank(PDDocument pdf) throws IOException {
        PDFRenderer renderer = new PDFRenderer(pdf);
        BufferedImage bufferedImage = renderer.renderImage(0);
//        ImageIO.write(bufferedImage, "JPEG", new File("D:\\s2\\myimage2.jpg"));
        long count = 0;
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        Double areaFactor = (width * height) * 0.99;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color c = new Color(bufferedImage.getRGB(x, y));
                // verify light gray and white
                if (c.getRed() == c.getGreen() && c.getRed() == c.getBlue()
                        && c.getRed() >= 248) {
                    count++;
                }
            }
        }

        if (count >= areaFactor) {
            return true;
        }

        return false;
    }
    
    
    public static Boolean isBlank(PDPage page) throws IOException {
        List<COSBase> operands = (List<COSBase>) page.getResources().getCOSObject();
        BufferedImage bufferedImage = null;
        for(int i = 0; i < operands.size(); i++){
            COSName objectName = (COSName) operands.get( i );
            PDXObject xobject = page.getResources().getXObject( objectName );
            if( xobject instanceof PDImageXObject)
            {
                PDImageXObject image = (PDImageXObject)xobject;
                bufferedImage = image.getImage();
                ImageIO.write(bufferedImage, "JPEG", new File("D:\\s2\\myimage2.jpg"));
            }
        }
        
//        PDFRenderer renderer = new PDFRenderer(pdf);
//        BufferedImage bufferedImage = renderer.renderImage(0);
        long count = 0;
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        Double areaFactor = (width * height) * 0.99;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color c = new Color(bufferedImage.getRGB(x, y));
                // verify light gray and white
                if (c.getRed() == c.getGreen() && c.getRed() == c.getBlue()
                        && c.getRed() >= 248) {
                    count++;
                }
            }
        }

        if (count >= areaFactor) {
            return true;
        }

        return false;
    }

    //resaving to remove strange issue
    //pdf was getting blank in few cases
    //resaving was solving that bug
    public static File cleanFile(String path){
        PDFMergerUtility pdfsp1 = new PDFMergerUtility();
        pdfsp1.setDestinationFileName(path);
        pdfsp1.addSource(new File(path));
        pdfsp1.mergeDocuments(null);
        return new File(path);
    }
}
