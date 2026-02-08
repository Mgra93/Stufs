package hr.algebra.cugomatfx.models;

import hr.algebra.cugomatfx.CugomatFXApplication;
import hr.algebra.cugomatfx.helpers.AppConfig;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import javafx.application.Application;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class StickerGenerator {
    private static final String JASPER_TABLE_STICKER_PATH = AppConfig.get("jasper.sticker.table.path");
    private static final String LOGO_PATH = AppConfig.get("img.logo.path");
    private static final String BACKGROUND_PATH = AppConfig.get("img.background.path");
    private static final String OUTPUT_DIR = AppConfig.get("jasper.sticker.out.dir");

    public void createTableStickerPdf(BarcodeData barcodeData, String tableCode, String webUrl) throws Exception {
        InputStream jasperStream = getClass().getResourceAsStream(JASPER_TABLE_STICKER_PATH);
        if (jasperStream == null) {
            throw new FileNotFoundException("JASPER file not found at: " + JASPER_TABLE_STICKER_PATH);
        }

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

        InputStream logoStream = getClass().getResourceAsStream(LOGO_PATH);
        if (logoStream == null) {
            throw new FileNotFoundException("Logo not found at: " + LOGO_PATH);
        }
        Image logoImage = ImageIO.read(logoStream);

        InputStream backgroundStream = getClass().getResourceAsStream(BACKGROUND_PATH);
        if (backgroundStream == null) {
            throw new FileNotFoundException("Background not found at: " + BACKGROUND_PATH);
        }
        Image backgroundImage = ImageIO.read(backgroundStream);

        Map<String, Object> params = new HashMap<>();
        params.put("LOGO", logoImage);
        params.put("WEB_URL", MessageHelper.getString("jasper.table.sticker.txt.visit") + webUrl);
        params.put("TABLE_CODE", tableCode);
        params.put("BACKGROUND", backgroundImage);
        params.put("QR_DATA", barcodeData.toJson());
        params.put("TXT_SCAN", MessageHelper.getString("jasper.table.sticker.txt.scan"));
        params.put("TXT_TABLE", MessageHelper.getString("jasper.table.sticker.txt.table"));

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

        File outputDir = new File(OUTPUT_DIR);

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Failed to create output folder: " + OUTPUT_DIR);
        }

        String outputPdfPath = OUTPUT_DIR + "tableSticker_" + CugomatFXApplication.getCurrentWorker().getClient().getCode() + "_" + tableCode + ".pdf";
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPdfPath);

        System.out.println("PDF generated at: " + outputPdfPath);
    }
}
