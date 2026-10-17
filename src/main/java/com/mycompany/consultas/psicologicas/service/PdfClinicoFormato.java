package com.mycompany.consultas.psicologicas.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.*;

/** Presentación PDF: no altera datos clínicos ni invoca IA. */
public final class PdfClinicoFormato {
    private PdfClinicoFormato() {}

    private static final Font TEXTO = FontFactory.getFont(FontFactory.HELVETICA, 10.5f, BaseColor.BLACK);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10.5f, BaseColor.BLACK);
    private static final Font CURSIVA = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10.5f, BaseColor.BLACK);
    private static final Font SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, new BaseColor(40, 53, 68));
    private static final Pattern TOKENS = Pattern.compile("(?s)<[^>]*>|[^<]+");
    private static final Pattern MARKDOWN = Pattern.compile("(\\*\\*[^*\\r\\n]+\\*\\*|__[^_\\r\\n]+__)");

    public static Document documento() {
        return new Document(PageSize.A4, 52, 52, 58, 55);
    }

    public static void cabecera(Document doc, String titulo, String paciente, String fechaDetalle) throws DocumentException {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{65, 35});
        PdfPCell izq = new PdfPCell();
        izq.setBorder(Rectangle.NO_BORDER);
        izq.setPaddingBottom(10);
        // Logo opcional: funciona tanto desde NetBeans como dentro del JAR.
        try {
            java.net.URL recurso = PdfClinicoFormato.class.getResource("/static/brain.png");
            if (recurso != null) {
                Image logo = Image.getInstance(recurso);
                logo.scaleToFit(32, 32);
                izq.addElement(logo);
            }
        } catch (Exception ignored) { /* El PDF no debe fallar por un logo ausente. */ }
        izq.addElement(new Paragraph("Gestión Psicológica", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        PdfPCell der = new PdfPCell(new Phrase("Emitido: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                FontFactory.getFont(FontFactory.HELVETICA, 8.5f, BaseColor.DARK_GRAY)));
        der.setBorder(Rectangle.NO_BORDER);
        der.setHorizontalAlignment(Element.ALIGN_RIGHT);
        der.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tabla.addCell(izq);
        tabla.addCell(der);
        doc.add(tabla);
        Paragraph t = new Paragraph(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(35, 50, 65)));
        t.setAlignment(Element.ALIGN_CENTER);
        t.setSpacingBefore(9);
        t.setSpacingAfter(16);
        doc.add(t);
        Paragraph datos = new Paragraph();
        datos.setLeading(16);
        datos.add(new Chunk("Paciente: ", NEGRITA));
        datos.add(new Chunk(paciente == null ? "" : paciente, TEXTO));
        doc.add(datos);
        if (fechaDetalle != null && !fechaDetalle.isBlank()) {
            Paragraph f = new Paragraph(fechaDetalle, TEXTO);
            f.setSpacingAfter(14);
            doc.add(f);
        } else {
            doc.add(Chunk.NEWLINE);
        }
        PdfPTable separador = new PdfPTable(1);
        separador.setWidthPercentage(100);
        PdfPCell linea = new PdfPCell(new Phrase(" "));
        linea.setFixedHeight(4);
        linea.setBorder(Rectangle.BOTTOM);
        linea.setBorderColor(new BaseColor(190, 198, 205));
        separador.addCell(linea);
        separador.setSpacingAfter(12);
        doc.add(separador);
    }

    public static void seccion(Document doc, String titulo) throws DocumentException {
        Paragraph p = new Paragraph(titulo, SUBTITULO);
        p.setSpacingBefore(10);
        p.setSpacingAfter(7);
        p.setKeepTogether(true);
        doc.add(p);
    }

    public static void contenido(Document doc, String original) throws DocumentException {
        if (original == null || original.isBlank()) {
            parrafo(doc, new Paragraph("Sin contenido registrado.", TEXTO), false);
            return;
        }
        String html = original.replace("\r\n", "\n").replace('\r', '\n');
        // Saltos de línea del texto simple se conservan; en HTML, <p>, <div> y <br> son separadores.
        boolean contieneHtml = Pattern.compile("(?i)</?(p|div|br|h[1-6]|li|ul|ol|strong|em|span|b|i)\\b").matcher(html).find();
        if (!contieneHtml) {
            for (String linea : html.split("\\n", -1)) {
                if (linea.trim().isEmpty()) continue;
                boolean titulo = linea.trim().matches("(?s)^(?:#{1,6}\\s+.+|\\*\\*[^*]+\\*\\*)$");
                Paragraph p = new Paragraph();
                if (titulo) {
                    String limpio = linea.trim().replaceFirst("^#{1,6}\\s+", "").replaceAll("^\\*\\*|\\*\\*$", "");
                    p.add(new Chunk(limpio, SUBTITULO));
                } else {
                    agregarMarkdown(p, decodificar(linea.trim()), TEXTO);
                }
                parrafo(doc, p, titulo);
            }
            return;
        }
        Matcher m = TOKENS.matcher(html);
        Paragraph actual = nuevoParrafo();
        boolean negrita = false, cursiva = false, encabezado = false;
        boolean lista = false;
        while (m.find()) {
            String token = m.group();
            if (!token.startsWith("<")) {
                String texto = decodificar(token).replace('\n', ' ');
                if (!texto.isEmpty()) {
                    agregarMarkdown(actual, texto, encabezado ? SUBTITULO : negrita ? NEGRITA : cursiva ? CURSIVA : TEXTO);
                }
                continue;
            }
            String tag = token.toLowerCase(java.util.Locale.ROOT).replaceAll("^<\\s*/?\\s*([a-z0-9]+).*", "$1");
            boolean cierre = token.matches("(?s)<\\s*/.*");
            if (tag.matches("h[1-6]")) {
                if (!cierre) {
                    if (!actual.getChunks().isEmpty()) { parrafo(doc, actual, encabezado); actual = nuevoParrafo(); }
                    encabezado = true;
                } else {
                    parrafo(doc, actual, true); actual = nuevoParrafo(); encabezado = false;
                }
            } else if (tag.equals("p") || tag.equals("div") || tag.equals("li")) {
                if (!cierre) {
                    if (!actual.getChunks().isEmpty()) { parrafo(doc, actual, encabezado); actual = nuevoParrafo(); }
                    if (tag.equals("li")) actual.add(new Chunk("•  ", TEXTO));
                } else {
                    parrafo(doc, actual, encabezado); actual = nuevoParrafo();
                }
            } else if (tag.equals("br")) {
                if (!actual.getChunks().isEmpty()) { parrafo(doc, actual, encabezado); actual = nuevoParrafo(); }
            } else if (tag.equals("strong") || tag.equals("b")) {
                negrita = !cierre;
            } else if (tag.equals("em") || tag.equals("i")) {
                cursiva = !cierre;
            }
        }
        if (!actual.getChunks().isEmpty()) parrafo(doc, actual, encabezado);
    }

    private static Paragraph nuevoParrafo() {
        Paragraph p = new Paragraph();
        p.setLeading(0, 1.35f);
        return p;
    }

    private static void parrafo(Document doc, Paragraph p, boolean encabezado) throws DocumentException {
        if (p.getChunks().isEmpty()) return;
        p.setAlignment(encabezado ? Element.ALIGN_LEFT : Element.ALIGN_JUSTIFIED);
        p.setSpacingBefore(encabezado ? 10 : 0);
        p.setSpacingAfter(encabezado ? 8 : 7);
        p.setKeepTogether(encabezado);
        doc.add(p);
    }

    private static void agregarMarkdown(Paragraph p, String texto, Font base) {
        Matcher m = MARKDOWN.matcher(texto);
        int inicio = 0;
        while (m.find()) {
            if (m.start() > inicio) p.add(new Chunk(texto.substring(inicio, m.start()), base));
            String valor = m.group();
            p.add(new Chunk(valor.substring(2, valor.length() - 2), NEGRITA));
            inicio = m.end();
        }
        if (inicio < texto.length()) p.add(new Chunk(texto.substring(inicio), base));
    }

    private static String decodificar(String s) {
        return s.replace("&nbsp;", " ").replace("&#160;", " ")
                .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
                .replace("&quot;", "\"").replace("&#39;", "'")
                .replace("&aacute;", "á").replace("&eacute;", "é")
                .replace("&iacute;", "í").replace("&oacute;", "ó")
                .replace("&uacute;", "ú").replace("&ntilde;", "ñ")
                .replace("&Aacute;", "Á").replace("&Eacute;", "É")
                .replace("&Iacute;", "Í").replace("&Oacute;", "Ó")
                .replace("&Uacute;", "Ú").replace("&Ntilde;", "Ñ");
    }
}
