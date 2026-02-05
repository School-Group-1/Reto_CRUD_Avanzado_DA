package report;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import model.Company;
import model.Product;
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
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // 3. Pasar los datos
            JRBeanCollectionDataSource dataSource
                    = new JRBeanCollectionDataSource(companies);

            // 4. Rellenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(
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
            JasperReport jasperReport
                    = JasperCompileManager.compileReport(reportStream);

            // 3. Pasar los datos
            JRBeanCollectionDataSource dataSource
                    = new JRBeanCollectionDataSource(java.util.Collections.singletonList(profile));

            // 4. Rellenar el reporte
            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(
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

    public void generateUsersReport(List<Profile> users) {
        try {

            InputStream reportStream = getClass()
                    .getResourceAsStream("/report/users_list_report.jrxml");

            JasperReport jasperReport
                    = JasperCompileManager.compileReport(reportStream);

            JRBeanCollectionDataSource dataSource
                    = new JRBeanCollectionDataSource(users);

            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(
                            jasperReport,
                            new HashMap<>(),
                            dataSource
                    );

            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateCompanyProductsReport(Company company, List<Product> products) {
        try {
            InputStream reportStream = getClass()
                    .getResourceAsStream("/report/company_products_report.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            HashMap<String, Object> parameters = new HashMap<>();

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(products);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    dataSource
            );

            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateCompleteReport(List<Company> companies) {
        try {
            // Cargar reporte principal
            InputStream reportStream = getClass()
                    .getResourceAsStream("/report/companies_complete_report.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Parámetros (opcional: fecha)
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_DATE", new java.util.Date());

            // DATOS: Lista de Companies
            JRBeanCollectionDataSource dataSource
                    = new JRBeanCollectionDataSource(companies);

            // GENERAR REPORTE
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, dataSource);

            // MOSTRAR
            JasperViewer.viewReport(jasperPrint, false);

            System.out.println("✅ REPORTE GENERADO CORRECTAMENTE");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
