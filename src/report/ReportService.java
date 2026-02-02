package report;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import model.Company;
import model.Profile;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class ReportService {

    public void generateCompaniesReport(List<Company> companies) {

        try {
            // 1. Cargar el diseño jrxml
            InputStream reportStream = getClass()
                    .getResourceAsStream("/report/companies_report.jrxml");

            // 2. Compilar el reporte
            JasperReport jasperReport =
                    JasperCompileManager.compileReport(reportStream);

            // 3. Pasar los datos
            JRBeanCollectionDataSource dataSource =
                    new JRBeanCollectionDataSource(companies);

            // 4. Rellenar el reporte
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            jasperReport,
                            new HashMap<>(),
                            dataSource
                    );

            // 5. Mostrar el PDF
            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void generateUserReport(Profile profile) {

        try {
            // 1. Cargar el diseño jrxml
            InputStream reportStream = getClass()
                    .getResourceAsStream("/report/profile_report.jrxml");

            // 2. Compilar el reporte
            JasperReport jasperReport =
                    JasperCompileManager.compileReport(reportStream);

            // 3. Pasar los datos
            JRBeanCollectionDataSource dataSource =
                    new JRBeanCollectionDataSource(java.util.Collections.singletonList(profile));

            // 4. Rellenar el reporte
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            jasperReport,
                            new HashMap<>(),
                            dataSource
                    );

            // 5. Mostrar el PDF
            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
