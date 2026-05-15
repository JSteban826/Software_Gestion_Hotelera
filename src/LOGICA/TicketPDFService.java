package LOGICA;

import static LOGICA.enviarCorreoConAdjunto.enviarCorreoConAdjunto;
import PERSISTENCIA.ConexionBD;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.sql.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketPDFService {

    public static class ParametrosReserva {

        public String idReserva;
        public String idCliente;
        public String idHabitacion;
        public Date fechaEntrada;
        public Date fechaSalida;
        public String correo;
        public String metodoPago; // 💳 nuevo campo
    }

    public static class ParametrosCheckOut {

        public String idCheck;
        public String idCliente;
        public String nombreCliente;
        public String correo;
        public String diasEstancia;
        public String valorTotal;
        public boolean minibar, restaurante, spa, habitacion;
        public String metodoPago; // 💳 nuevo campo
    }

    public static void generarFacturaReserva(ParametrosReserva pr) throws Exception {
        Document documento = new Document();
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String nombreArchivo = "Reserva_" + pr.idCliente + "_" + fechaActual + ".pdf";

        File directorio = new File("Facturas_reserva");
        if (!directorio.exists()) {
            throw new TicketNoGeneradoException(CodigoError.ERR_GENERAR_TICKET,
                    "La ruta para guardar la factura no existe: " + directorio.getAbsolutePath());
        }

        String ruta = directorio.getAbsolutePath() + File.separator + "Ticket_" + nombreArchivo;
        PdfWriter.getInstance(documento, new FileOutputStream(ruta));
        documento.open();

        // Aquí metes el código de logo + encabezado
        generarEncabezado(documento, "Ticket de Reserva", pr.idCliente, fechaActual);

        // Datos de la reserva
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10);

        tabla.addCell(getCeldaServicioTabla("ID Reserva:"));
        tabla.addCell(getCeldaValorTabla(pr.idReserva));
        tabla.addCell(getCeldaServicioTabla("ID Cliente:"));
        tabla.addCell(getCeldaValorTabla(pr.idCliente));
        tabla.addCell(getCeldaServicioTabla("ID Habitación:"));
        tabla.addCell(getCeldaValorTabla(pr.idHabitacion));
        tabla.addCell(getCeldaServicioTabla("Fecha Entrada:"));
        tabla.addCell(getCeldaValorTabla(new SimpleDateFormat("yyyy-MM-dd").format(pr.fechaEntrada)));
        tabla.addCell(getCeldaServicioTabla("Fecha Salida:"));
        tabla.addCell(getCeldaValorTabla(new SimpleDateFormat("yyyy-MM-dd").format(pr.fechaSalida)));
        tabla.addCell(getCeldaServicioTabla("Método de Pago:"));
        tabla.addCell(getCeldaValorTabla(pr.metodoPago));

        documento.add(tabla);
        documento.close();

        enviarCorreoConAdjunto(pr.correo, "Ticket Reserva Hotel",
                "Adjunto encontrará su ticket de reserva.", ruta);
    }

    public static void generarFacturaCheckOut(ParametrosCheckOut pchk) throws Exception {
        Document documento = new Document();
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String nombreArchivo = "Check_out_" + pchk.idCliente + "_" + fechaActual + ".pdf";

        File directorio = new File("Facturas_check_out");
        if (!directorio.exists()) {
            throw new TicketNoGeneradoException(CodigoError.ERR_GENERAR_TICKET,
                    "La ruta para guardar la factura no existe: " + directorio.getAbsolutePath());
        }

        String ruta = directorio.getAbsolutePath() + File.separator + "Ticket_" + nombreArchivo;
        PdfWriter.getInstance(documento, new FileOutputStream(ruta));
        documento.open();

        generarEncabezado(documento, "Ticket de Check-Out", pchk.idCliente, fechaActual);

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10);

        tabla.addCell(getCeldaServicioTabla("Cliente:"));
        tabla.addCell(getCeldaValorTabla(pchk.nombreCliente));
        tabla.addCell(getCeldaServicioTabla("ID Cliente:"));
        tabla.addCell(getCeldaValorTabla(pchk.idCliente));
        tabla.addCell(getCeldaServicioTabla("Correo:"));
        tabla.addCell(getCeldaValorTabla(pchk.correo));
        tabla.addCell(getCeldaServicioTabla("ID Check:"));
        tabla.addCell(getCeldaValorTabla(pchk.idCheck));
        tabla.addCell(getCeldaServicioTabla("Días Estancia:"));
        tabla.addCell(getCeldaValorTabla(pchk.diasEstancia));
        tabla.addCell(getCeldaServicioTabla("Método de Pago:"));
        tabla.addCell(getCeldaValorTabla(pchk.metodoPago));

        documento.add(tabla);

        double totalMinibar = 0.0;

        if (pchk.minibar) {
            String sql = "SELECT total_cuenta FROM consumo_minibar WHERE id_cliente = ? ORDER BY fecha_consumo DESC LIMIT 1";

            try (Connection conn = ConexionBD.conectar(); PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, pchk.idCliente);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        totalMinibar = rs.getDouble("total_cuenta");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Servicios adicionales
        PdfPTable tablaServicios = new PdfPTable(2);
        tablaServicios.setWidthPercentage(100);
        tablaServicios.setSpacingBefore(10);

        if (pchk.minibar) {
            tablaServicios.addCell(getCeldaServicioTabla("Minibar"));
            tablaServicios.addCell(getCeldaValorTabla("$" + String.format("%.2f", totalMinibar)));
        }

        if (pchk.restaurante) {
            tablaServicios.addCell(getCeldaServicioTabla("Restaurante"));
            tablaServicios.addCell(getCeldaValorTabla("$25000"));
        }
        if (pchk.spa) {
            tablaServicios.addCell(getCeldaServicioTabla("Spa"));
            tablaServicios.addCell(getCeldaValorTabla("$40000"));
        }
        if (pchk.habitacion) {
            tablaServicios.addCell(getCeldaServicioTabla("Daño Habitación"));
            tablaServicios.addCell(getCeldaValorTabla("$80000"));
        }

        tablaServicios.addCell(getCeldaServicioTabla("TOTAL"));
        tablaServicios.addCell(getCeldaValorTabla("$" + pchk.valorTotal));

        documento.add(tablaServicios);

        documento.close();

        enviarCorreoConAdjunto(pchk.correo, "Ticket Check-Out Hotel",
                "Adjunto encontrará su ticket de check-out.", ruta);
    }

    private static void generarEncabezado(Document documento, String titulo, String idCliente, String fecha) throws Exception {
        Image logo = Image.getInstance(TicketPDFService.class.getResource("/com/images/coral.png"));
        logo.scaleAbsolute(60, 60);

        PdfPTable tablaEncabezado = new PdfPTable(2);
        tablaEncabezado.setWidthPercentage(100);

        PdfPCell celdaNombre = new PdfPCell(new Phrase("Resort Bahía Coral",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
        celdaNombre.setBorder(Rectangle.NO_BORDER);

        PdfPCell celdaLogo = new PdfPCell(logo);
        celdaLogo.setBorder(Rectangle.NO_BORDER);
        celdaLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);

        tablaEncabezado.addCell(celdaNombre);
        tablaEncabezado.addCell(celdaLogo);

        documento.add(tablaEncabezado);
        documento.add(new Paragraph("N°Ticket:/" + idCliente + "/" + fecha + "/",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        documento.add(new Paragraph(titulo,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY)));
        documento.add(Chunk.NEWLINE);
    }

    private static PdfPCell getCeldaServicioTabla(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setPadding(5);
        return celda;
    }

    private static PdfPCell getCeldaValorTabla(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setPadding(5);
        return celda;
    }
}
