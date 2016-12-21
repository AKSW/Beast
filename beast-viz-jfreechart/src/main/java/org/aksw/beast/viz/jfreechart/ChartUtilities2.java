package org.aksw.beast.viz.jfreechart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.jfree.chart.JFreeChart;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Source: http://www.jfree.org/phpBB2/viewtopic.php?f=3&t=17629
 *
 * @author raven
 *
 */
public class ChartUtilities2 {
	public static void saveChartAsPDF(File file, JFreeChart chart, int width, int height)
			throws DocumentException, FileNotFoundException
	{
		OutputStream out = new FileOutputStream(file);
		saveChartAsPDF(out, chart, width, height);
	}

	public static void saveChartAsPDF(OutputStream out, JFreeChart chart, int width, int height)
			throws DocumentException
	{
		Rectangle pagesize = new Rectangle(width, height);
		Document document = new Document(pagesize, 50, 50, 50, 50);
		PdfWriter writer = PdfWriter.getInstance(document, out);
		document.open();
		PdfContentByte cb = writer.getDirectContent();
		PdfTemplate tp = cb.createTemplate(width, height);
		Graphics2D g2 = tp.createGraphics(width, height, new DefaultFontMapper());
		Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
		chart.draw(g2, r2D);
		g2.dispose();
		cb.addTemplate(tp, 0, 0);
		document.close();
	}

}
