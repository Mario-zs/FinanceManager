package clases;

import java.io.File;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

public class ReportePDF {

    public static void generarHistorial(String user, String filtroTipo, String filtroMes, String filtroAnio, double totalIngresos, double totalEgresos, List<Movimiento> movimientos) {

        PDDocument document = new PDDocument();

        try {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;

            //Encabezado
            yStart = dibujarEncabezado(document, content, margin, yStart, user);

            //Resumen
            yStart = dibujarResumenFinanciero(content, margin, yStart, totalIngresos, totalEgresos);

            //Filtros
            yStart = dibujarFiltrosAplicados(content, margin, yStart, filtroTipo, filtroMes, filtroAnio);

            //Tabla
            yStart = dibujarTablaMovimientos(document, content, page, margin, yStart, movimientos);

            agregarPieDePagina(document);

            content.close();

            //Guardar
            String nombreArchivo = generarNombreArchivo(user, filtroTipo, filtroMes, filtroAnio);
            File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
            File file = new File(desktopDir, nombreArchivo);
            document.save(file);
            document.close();

        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
        }

    }

    private static float dibujarEncabezado(PDDocument document, PDPageContentStream content, float x, float y, String user) throws Exception {

        //Logo
        try (InputStream is = ReportePDF.class.getResourceAsStream("/images/Logo.png")) {
            if (is != null) {
                PDImageXObject logo = PDImageXObject.createFromByteArray(
                        document,
                        is.readAllBytes(),
                        "logo"
                );
                content.drawImage(logo, x, y - 40, 40, 40);
            }
        }

        //Titulo
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        content.newLineAtOffset(x + 60, y - 20);
        content.showText("Finance Manager");
        content.endText();

        // SUBTÍTULO
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        content.newLineAtOffset(x + 60, y - 40);
        content.showText("Reporte de movimientos");
        content.endText();

        // USUARIO + FECHA
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        content.newLineAtOffset(x, y - 70);
        content.showText("Usuario: " + user);
        content.endText();

        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        content.newLineAtOffset(x, y - 85);
        content.showText("Fecha de generación: " + LocalDate.now());
        content.endText();

        return y - 110; // nueva posición Y
    }

    private static float dibujarResumenFinanciero(PDPageContentStream content, float x, float y, double ingresos, double egresos) throws Exception {

        double saldo = ingresos - egresos;

        float boxWidth = 160;
        float boxHeigth = 50;
        float gap = 10;

        //Titulo
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        content.newLineAtOffset(x, y);
        content.showText("Resumen financiero");
        content.endText();

        y -= 20;

        //Ingresos
        dibujarCaja(content, x, y, boxWidth, boxHeigth, "Ingresos", ingresos);

        //Egresos
        dibujarCaja(content, x + boxWidth + gap, y, boxWidth, boxHeigth, "Egresos", egresos);

        //Saldo
        dibujarCaja(content, x + (boxWidth + gap) * 2, y, boxWidth, boxHeigth, "Saldo", saldo);

        return y - boxHeigth - 20;

    }

    private static void dibujarCaja(PDPageContentStream content, float x, float y, float width, float heigth, String titulo, double valor) throws Exception {

        //Rectangulo
        content.addRect(x, y - heigth, width, heigth);
        content.stroke();

        //Titulo
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        content.newLineAtOffset(x + 8, y - 15);
        content.showText(titulo);
        content.endText();

        //Valor
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        content.newLineAtOffset(x + 8, y - 35);
        content.showText(String.format("$ %,.2f", valor));
        content.endText();
    }

    private static float dibujarFiltrosAplicados(PDPageContentStream content, float x, float y, String tipo, String mes, String anio) throws Exception {

        //Noarmalizar valores
        tipo = normalizarFiltro(tipo);
        mes = normalizarFiltro(mes);
        anio = normalizarFiltro(anio);

        //Titulo
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        content.newLineAtOffset(x, y);
        content.showText("Filtros aplicados");
        content.endText();

        y -= 18;

        //Filtros
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        content.newLineAtOffset(x, y);
        content.showText("Tipo: " + tipo + " | Mes: " + mes + " | Año: " + anio);
        content.endText();

        return y - 25;
    }

    private static String normalizarFiltro(String valor) {
        if (valor == null || valor.isBlank() || valor.equalsIgnoreCase("Todos")) {
            return "Todos";
        }

        return valor;
    }

    private static float dibujarTablaMovimientos(PDDocument document, PDPageContentStream content, PDPage page, float margin, float y, List<Movimiento> movimientos) throws Exception {

        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float rowHeigth = 18;
        float yPosition = y;

        //Columnas
        float[] colWidths = {40, 100, 100, 100};
        String[] headers = {"#", "Tipo", "Monto", "Fecha"};

        //Encabezado
        yPosition = dibujarEncabezadoTabla(content, margin, yPosition, colWidths, headers);

        int index = 1;

        for (Movimiento m : movimientos) {

            //Salto de pagina
            if (yPosition < 80) {
                content.close();

                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                content = new PDPageContentStream(document, page);

                yPosition = page.getMediaBox().getHeight() - margin;

                yPosition = dibujarEncabezadoTabla(content, margin, yPosition, colWidths, headers);
            }

            //Fila
            yPosition = dibujarFilaMovimiento(content, margin, yPosition, colWidths, index++, m);
        }

        return yPosition;
    }

    private static float dibujarEncabezadoTabla(PDPageContentStream content, float x, float y, float[] colWidths, String[] headers) throws Exception {

        float textY = y - 14;
        float cursorX = x;

        content.setLineWidth(1);

        for (int i = 0; i < headers.length; i++) {
            content.addRect(cursorX, y - 18, colWidths[i], 18);
            content.stroke();

            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
            content.newLineAtOffset(cursorX + 4, textY);
            content.showText(headers[i]);
            content.endText();

            cursorX += colWidths[i];
        }

        return y - 18;
    }

    private static float dibujarFilaMovimiento(PDPageContentStream content, float x, float y, float[] colWidths, int index, Movimiento m) throws Exception {

        float textY = y - 14;
        float cursorX = x;

        String[] values = {String.valueOf(index), m.getTipo(), String.format("$ %,.2f", m.getMonto()), m.getFecha().toString()};

        for (int i = 0; i < values.length; i++) {
            content.addRect(cursorX, y - 18, colWidths[i], 18);
            content.stroke();

            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            content.newLineAtOffset(cursorX + 4, textY);
            content.showText(values[i]);
            content.endText();

            cursorX += colWidths[i];
        }

        y -= 18;

        //Comentarios
        if (m.getComentarios() != null && !m.getComentarios().isBlank()) {

            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
            float fontSize = 9;
            float maxWidth = 400; // ancho disponible dentro del margen

            List<String> lineas = dividirTexto(
                    "Comentario: " + m.getComentarios(),
                    font,
                    fontSize,
                    maxWidth
            );

            for (String linea : lineas) {
                content.beginText();
                content.setFont(font, fontSize);
                content.newLineAtOffset(x + 10, y - 12);
                content.showText(linea);
                content.endText();
                y -= 12;
            }

            y -= 4;
        }

        return y;
    }

    private static List<String> dividirTexto(
            String texto,
            PDType1Font font,
            float fontSize,
            float maxWidth) throws IOException {

        List<String> lineas = new java.util.ArrayList<>();
        String[] palabras = texto.split(" ");
        StringBuilder lineaActual = new StringBuilder();

        for (String palabra : palabras) {

            String posibleLinea = lineaActual.length() == 0
                    ? palabra
                    : lineaActual + " " + palabra;

            float ancho = font.getStringWidth(posibleLinea) / 1000 * fontSize;

            if (ancho > maxWidth) {
                lineas.add(lineaActual.toString());
                lineaActual = new StringBuilder(palabra);
            } else {
                lineaActual = new StringBuilder(posibleLinea);
            }
        }

        if (!lineaActual.isEmpty()) {
            lineas.add(lineaActual.toString());
        }

        return lineas;
    }

    private static void agregarPieDePagina(PDDocument document) throws Exception {

        int totalPaginas = document.getNumberOfPages();

        for (int i = 0; i < totalPaginas; i++) {

            PDPage page = document.getPage(i);

            PDPageContentStream content = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

            PDRectangle box = page.getMediaBox();
            float y = 30;

            // Texto izquierdo
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
            content.newLineAtOffset(50, y);
            content.showText("Finance Manager · Reporte de movimientos");
            content.endText();

            // Texto derecho (número de página)
            String pagina = "Página " + (i + 1) + " de " + totalPaginas;

            float textWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA).getStringWidth(pagina) / 1000 * 9;

            content.beginText();
            content.newLineAtOffset(box.getWidth() - 50 - textWidth, y);
            content.showText(pagina);
            content.endText();

            content.close();
        }
    }

    private static String generarNombreArchivo(String user, String tipo, String mes, String anio) {

        //Normalizar
        tipo = normalizarArchivo(tipo);
        mes = normalizarArchivo(mes);
        anio = normalizarArchivo(anio);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

        String fechaHora = LocalDateTime.now().format(formatter);

        return String.format("Historial_%s_%s_%s_%s_%s.pdf", user, tipo, mes, anio, fechaHora);
    }

    private static String normalizarArchivo(String texto) {

        if (texto == null || texto.equalsIgnoreCase("Todos")) {
            return "Todos";
        }

        return texto.replace(" ", "_").replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
    }

}
