package com.example.qoppapdfoptimizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.qoppa.pdfOptimizer.ImageHandler;
import com.qoppa.pdfOptimizer.ImageInfo;
import com.qoppa.pdfOptimizer.ImageOutput;
import com.qoppa.pdfOptimizer.OptSettings;
import com.qoppa.pdfOptimizer.PDFOptimizer;

public class PdfOptimizerHandling implements ImageHandler, PropertyChangeListener{

//    private static final String ORIG = "./src/main/resources/pdfoptimizer/FDV.pdf";
//    private static final String OUTPUT_FILE = "./target/sandbox/pdfoptimizer/FDV_optimized.pdf";

//    private static final String ORIG = "./src/main/resources/pdfoptimizer/FDV-2.pdf";
//    private static final String OUTPUT_FILE = "./target/sandbox/pdfoptimizer/FDV-2_optimized.pdf";

    private static final String ORIG = "./src/main/resources/pdfoptimizer/FDV-3.pdf";
    private static final String OUTPUT_FILE = "./target/sandbox/pdfoptimizer/FDV-3_optimized.pdf";

    public static void main (String [] args)
    {
        try
        {
            // Load a document
            PDFOptimizer pdfOptimize = new PDFOptimizer(ORIG, null);

            // set optimization options
            OptSettings options = new OptSettings();

            // Options to remove objects
            options.setDiscardAltImages(true);
            options.setDiscardAnnotations(true);
            options.setDiscardBookmarks(true);
            options.setDiscardDocumentInfo(true);
            options.setDiscardFileAttachments(true);
            options.setDiscardFormFields(true);
            options.setDiscardJSActions(true);
            options.setDiscardPageThumbnails(true);
            options.setDiscardXMPData(true);
            options.setDiscardUnusedResources(true);
            options.setDiscardLinks(true);

            // Other optimizing options
            options.setClearSignatures(true);
            options.setFlateUncompressedStreams(true);
            options.setMergeDuplicateFonts(true);
            options.setMergeDuplicateImages(true);
            options.setCompressObjectsIntoStreams(true);

            // create and set the image handler
            // This class SimpleOptimizer extends ImageHandler
            // and implements method convertImage (see below)
            PdfOptimizerHandling mySimpleOptimizer = new PdfOptimizerHandling();
            options.setImageHandler(mySimpleOptimizer);

            // add property change listener to get progress notifications
            // SimpleOptimizer extends PropertyChangeListener
            pdfOptimize.addPropertyChangeListener(mySimpleOptimizer);

            // Save the optimized document
            pdfOptimize.optimize(options, OUTPUT_FILE);

            // indicate that the file is done writing
            System.out.print("Done!");
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    // this is the method where you can implement any image conversion / optimization
    @Override
    public ImageOutput convertImage(ImageInfo imageInfo) {
        // construct an image output that by default retains the same image properties as the image input
        ImageOutput imageoutput = new ImageOutput(ImageOutput.CO_RETAIN, ImageOutput.CS_RETAIN, imageInfo.getImageWidth(), imageInfo.getImageHeight());

        // for gray or black and white images, use JBIG2 compressions
        if(imageInfo.isGray() || imageInfo.isMonochrome())
        {
            imageoutput.setCompression(ImageOutput.CO_JBIG2);
            imageoutput.setColorSpace(ImageOutput.CS_BW);
        }

        // for color images, use JPEG 2000 compression
        // and use a quality of 0.5
        if(imageInfo.isColor())
        {
            imageoutput.setCompression(ImageOutput.CO_JPEG2000);
            imageoutput.setCompressionQuality(0.5f);
        }

        // downgrade image resolution to 200DPI if it's higher
        if (imageInfo.getDPIX() > 200 || imageInfo.getDPIY() > 200)
        {
            // Calculate new dimensions to match DPI
            float scale = Math.min(200 / imageInfo.getDPIX(), 200 / imageInfo.getDPIY());
            int newWidth = (int)(imageInfo.getImageWidth() * scale + 0.5);
            int newHeight = (int)(imageInfo.getImageHeight() * scale + 0.5);
            imageoutput.setImageWidth(newWidth);
            imageoutput.setImageHeight(newHeight);
        }

        // return the new image output
        return imageoutput;
    }

    // write progress
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("IN PROGRESS " + (String)evt.getNewValue() + "...");
    }
}
