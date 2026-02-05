package report;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import model.CartItem;

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
    public void generateCartReport(List<CartItem> cartItems) {
        try {
            LOGGER.info("**ReportService** Generating cart report for " + cartItems.size() + " items");
            
            // Cargar el archivo JRXML (asegúrate de que está en /report/)
            InputStream reportStream = getClass()
                    .getResourceAsStream("/report/reporte_carrito_compras.jrxml");
            
            if (reportStream == null) {
                throw new IOException("No se encontró el archivo JRXML: /report/reporte_carrito_compras.jrxml");
            }
            
            // Compilar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            
            // Parámetros (pueden estar vacíos como en generateCompanyProductsReport)
            HashMap<String, Object> parameters = new HashMap<>();
            
            // Data source con los items del carrito
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(cartItems);
            
            // Generar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    dataSource
            );
            
            // Mostrar el reporte
            JasperViewer.viewReport(jasperPrint, false);
            
            LOGGER.info("**ReportService** Cart report generated successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "**ReportService** Error generating cart report", e);
            e.printStackTrace();
            throw new RuntimeException("Error al generar el reporte del carrito: " + e.getMessage(), e);
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
