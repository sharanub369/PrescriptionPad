package com.prescriptionpad.app.android.util;

/**
 * Created by sharana.b on 4/20/2017.
 */

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD, BaseColor.BLUE);
    private static Font subFontBlue = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.BOLD, BaseColor.BLUE);
    private static Font greenFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.BOLD, BaseColor.GREEN);
    private static Font blackFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD, BaseColor.BLACK);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD, BaseColor.BLACK);
    private static Font smallNormal = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL);

    private String mDoctorName = "";
    private String mFooterMessage = "";

    public HeaderFooterPageEvent(String mDoctorName, String mFooterMessage) {
        if (mDoctorName != null && !mDoctorName.isEmpty()) {
            this.mDoctorName = mDoctorName;
        } else {
            this.mDoctorName = "Doctor Name";
        }
        if (mFooterMessage != null && !mFooterMessage.isEmpty()) {
            this.mFooterMessage = mFooterMessage;
        } else {
            this.mFooterMessage = "Footer Message";
        }
    }

    //    public void onStartPage(PdfWriter writer,Document document) {
//        Rectangle rect = writer.getBoxSize("art");
//        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Top Left"), rect.getLeft(), rect.getTop(), 0);
//        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Top Right"), rect.getRight(), rect.getTop(), 0);
//    }
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Phrase header = new Phrase("this is a header", subFont);
        Phrase footer1 = new Phrase("...............................", subFontBlue);
        Phrase footer2 = new Phrase("(" + mDoctorName + ")", subFontBlue);
        Phrase footer3 = new Phrase(mFooterMessage, greenFont);

//        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
//                header,
//                (document.right() - document.left()) / 2 + document.leftMargin(),
//                document.top() + 10, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_JUSTIFIED,
                footer1,
                (document.right() + document.left()) + document.leftMargin() - 190,
                document.bottom() + 40, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_JUSTIFIED,
                footer2,
                (document.right() + document.left()) + document.leftMargin() - 190,
                document.bottom() + 25, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                footer3,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);
        //addSeparatorLine(writer, document);

    }

    private void addSeparatorLine(PdfWriter writer, Document document) {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.BLUE);
        ls.setLineWidth(2);
        Chunk chunk = new Chunk(ls, 200);
        try {
            writer.add(chunk);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

}