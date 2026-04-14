package com.crime.ui;

import com.crime.dao.CaseDAO;
import com.crime.dao.EvidenceDAO;
import com.crime.dao.SuspectDAO;
import com.crime.model.Case;
import com.crime.model.Evidence;
import com.crime.model.Suspect;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class InvestigationBoard extends Panel {

    private CaseDAO caseDAO         = new CaseDAO();
    private SuspectDAO suspectDAO   = new SuspectDAO();
    private EvidenceDAO evidenceDAO = new EvidenceDAO();

    private List<Case>     cases     = new ArrayList<>();
    private List<Suspect>  suspects  = new ArrayList<>();
    private List<Evidence> evidences = new ArrayList<>();

    private List<int[]> casePos     = new ArrayList<>();
    private List<int[]> suspectPos  = new ArrayList<>();
    private List<int[]> evidencePos = new ArrayList<>();

    private String hoveredInfo = "";
    private BoardCanvas canvas;

    private static final Color BG_COLOR      = new Color(10, 15, 30);
    private static final Color CASE_OPEN     = new Color(220, 60, 60);
    private static final Color CASE_INVEST   = new Color(255, 180, 0);
    private static final Color CASE_CLOSED   = new Color(60, 200, 100);
    private static final Color SUSPECT_COLOR = new Color(60, 130, 255);
    private static final Color EVIDENCE_COLOR= new Color(180, 60, 255);

    public InvestigationBoard() {
        setLayout(new BorderLayout(5, 5));
        setBackground(BG_COLOR);

        Panel header = new Panel(new BorderLayout());
        header.setBackground(new Color(15, 20, 40));
        header.setPreferredSize(new Dimension(0, 50));
        Label title = new Label("  INVESTIGATION BOARD  —  Case / Suspect / Evidence Map", Label.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        Button refreshBtn = new Button("Refresh Board");
        refreshBtn.setBackground(new Color(40, 55, 90));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> refresh());
        Panel bw = new Panel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bw.setBackground(new Color(15, 20, 40)); bw.add(refreshBtn);
        header.add(bw, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        canvas = new BoardCanvas();
        add(canvas, BorderLayout.CENTER);

        Panel legend = new Panel(new FlowLayout(FlowLayout.CENTER, 25, 6));
        legend.setBackground(new Color(15, 20, 40));
        legend.add(legendLabel("● OPEN",          CASE_OPEN));
        legend.add(legendLabel("● INVESTIGATING",  CASE_INVEST));
        legend.add(legendLabel("● CLOSED",         CASE_CLOSED));
        legend.add(legendLabel("■ SUSPECT",         SUSPECT_COLOR));
        legend.add(legendLabel("◆ EVIDENCE",        EVIDENCE_COLOR));
        add(legend, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        cases     = caseDAO.getAllCases();
        suspects  = suspectDAO.getAllSuspects();
        evidences = evidenceDAO.getAllEvidence();
        canvas.repaint();
    }

    private class BoardCanvas extends Canvas {
        BoardCanvas() {
            setBackground(BG_COLOR);
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) { checkHover(e.getX(), e.getY()); }
            });
        }

        public void paint(Graphics g) {
            int w = getWidth(), h = getHeight();
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG_COLOR); g2.fillRect(0, 0, w, h);
            drawGrid(g2, w, h);

            if (cases.isEmpty()) {
                g2.setColor(new Color(100, 120, 180));
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2.drawString("No data found. Add cases first.", w/2 - 180, h/2);
                return;
            }

            casePos.clear(); suspectPos.clear(); evidencePos.clear();

            int centerX = w/2, leftX = w/5, rightX = w*4/5;
            int nodeR = 28;

            for (int i = 0; i < cases.size(); i++)
                casePos.add(new int[]{centerX, (h/(cases.size()+1))*(i+1), cases.get(i).getCaseId()});
            for (int i = 0; i < suspects.size(); i++)
                suspectPos.add(new int[]{leftX, (h/(suspects.size()+1))*(i+1), suspects.get(i).getCaseId()});
            for (int i = 0; i < evidences.size(); i++)
                evidencePos.add(new int[]{rightX, (h/(evidences.size()+1))*(i+1), evidences.get(i).getCaseId()});

            // Draw lines
            g2.setStroke(new BasicStroke(1.5f));
            for (int[] sp : suspectPos)
                for (int[] cp : casePos)
                    if (cp[2] == sp[2]) drawLine(g2, sp[0], sp[1], cp[0], cp[1], new Color(60,130,255,130));
            for (int[] ep : evidencePos)
                for (int[] cp : casePos)
                    if (cp[2] == ep[2]) drawLine(g2, ep[0], ep[1], cp[0], cp[1], new Color(180,60,255,130));

            // Draw CASE nodes (circles)
            for (int i = 0; i < casePos.size(); i++) {
                int[] pos = casePos.get(i);
                Case c = cases.get(i);
                Color nc = switch (c.getStatus()) {
                    case "OPEN"          -> CASE_OPEN;
                    case "INVESTIGATING" -> CASE_INVEST;
                    case "CLOSED"        -> CASE_CLOSED;
                    default              -> Color.GRAY;
                };
                g2.setColor(new Color(nc.getRed(), nc.getGreen(), nc.getBlue(), 40));
                g2.fillOval(pos[0]-nodeR-10, pos[1]-nodeR-10, (nodeR+10)*2, (nodeR+10)*2);
                g2.setColor(nc);
                g2.fillOval(pos[0]-nodeR, pos[1]-nodeR, nodeR*2, nodeR*2);
                g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2));
                g2.drawOval(pos[0]-nodeR, pos[1]-nodeR, nodeR*2, nodeR*2);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                String num = c.getCaseNumber().replace("CAS-","");
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(num, pos[0]-fm.stringWidth(num)/2, pos[1]+4);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                fm = g2.getFontMetrics();
                String st = c.getTitle().length()>22 ? c.getTitle().substring(0,20)+"..." : c.getTitle();
                g2.setColor(new Color(220,230,255));
                g2.drawString(st, pos[0]-fm.stringWidth(st)/2, pos[1]+nodeR+16);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(nc);
                g2.drawString("["+c.getStatus()+"]", pos[0]-25, pos[1]+nodeR+30);
            }

            // Draw SUSPECT nodes (squares)
            for (int i = 0; i < suspectPos.size(); i++) {
                int[] pos = suspectPos.get(i);
                Suspect s = suspects.get(i);
                int sq = 22;
                Color dc = switch(s.getDangerLevel()) {
                    case "HIGH"   -> new Color(255,70,70);
                    case "MEDIUM" -> new Color(255,180,0);
                    default       -> new Color(60,200,100);
                };
                g2.setColor(new Color(SUSPECT_COLOR.getRed(), SUSPECT_COLOR.getGreen(), SUSPECT_COLOR.getBlue(), 40));
                g2.fillRect(pos[0]-sq-8, pos[1]-sq-8, (sq+8)*2, (sq+8)*2);
                g2.setColor(new Color(20,30,60)); g2.fillRect(pos[0]-sq, pos[1]-sq, sq*2, sq*2);
                g2.setColor(SUSPECT_COLOR); g2.setStroke(new BasicStroke(2.5f));
                g2.drawRect(pos[0]-sq, pos[1]-sq, sq*2, sq*2);
                g2.setColor(dc); g2.fillOval(pos[0]+sq-8, pos[1]-sq-2, 14, 14);
                g2.setColor(SUSPECT_COLOR);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString("S", pos[0]-5, pos[1]+6);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.setColor(new Color(150,190,255));
                String sn = s.getName().length()>14 ? s.getName().substring(0,13)+"..." : s.getName();
                g2.drawString(sn, pos[0]-sq, pos[1]+sq+15);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(dc);
                g2.drawString(s.getDangerLevel(), pos[0]-sq, pos[1]+sq+28);
            }

            // Draw EVIDENCE nodes (diamonds)
            for (int i = 0; i < evidencePos.size(); i++) {
                int[] pos = evidencePos.get(i);
                Evidence ev = evidences.get(i);
                int sq = 20;
                int[] xs = {pos[0], pos[0]+sq, pos[0], pos[0]-sq};
                int[] ys = {pos[1]-sq, pos[1], pos[1]+sq, pos[1]};
                g2.setColor(new Color(EVIDENCE_COLOR.getRed(), EVIDENCE_COLOR.getGreen(), EVIDENCE_COLOR.getBlue(), 35));
                g2.fillOval(pos[0]-sq-8, pos[1]-sq-8, (sq+8)*2, (sq+8)*2);
                g2.setColor(new Color(20,15,40)); g2.fillPolygon(xs, ys, 4);
                g2.setColor(EVIDENCE_COLOR); g2.setStroke(new BasicStroke(2.5f));
                g2.drawPolygon(xs, ys, 4);
                g2.setColor(EVIDENCE_COLOR);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                g2.drawString("E", pos[0]-5, pos[1]+5);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.setColor(new Color(200,160,255));
                String et = ev.getTitle().length()>14 ? ev.getTitle().substring(0,13)+"..." : ev.getTitle();
                g2.drawString(et, pos[0]-sq, pos[1]+sq+15);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(new Color(160,120,220));
                g2.drawString("["+ev.getType()+"]", pos[0]-sq, pos[1]+sq+28);
            }

            // Column labels
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(new Color(100,130,200)); g2.drawString("SUSPECTS", leftX-40, 30);
            g2.setColor(new Color(200,150,255)); g2.drawString("EVIDENCE", rightX-40, 30);
            g2.setColor(new Color(200,200,255)); g2.drawString("CASES", centerX-25, 30);

            // Dividers
            g2.setColor(new Color(40,55,90));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g2.drawLine(w/3, 40, w/3, h-10);
            g2.drawLine(w*2/3, 40, w*2/3, h-10);

            // Hover info bar
            if (!hoveredInfo.isEmpty()) {
                g2.setColor(new Color(20,30,60,220));
                g2.fillRoundRect(10, h-40, hoveredInfo.length()*7+20, 28, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString(hoveredInfo, 20, h-22);
            }
        }

        private void drawGrid(Graphics2D g2, int w, int h) {
            g2.setColor(new Color(25,35,60));
            g2.setStroke(new BasicStroke(0.5f));
            for (int x=0; x<w; x+=40) g2.drawLine(x, 0, x, h);
            for (int y=0; y<h; y+=40) g2.drawLine(0, y, w, y);
        }

        private void drawLine(Graphics2D g2, int x1, int y1, int x2, int y2, Color c) {
            g2.setColor(c); g2.setStroke(new BasicStroke(1.8f));
            g2.drawLine(x1, y1, x2, y2);
            int mx=(x1+x2)/2, my=(y1+y2)/2;
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
            g2.fillOval(mx-3, my-3, 6, 6);
        }
    }

    private void checkHover(int mx, int my) {
        hoveredInfo = "";
        int r = 30;
        for (int i=0; i<casePos.size(); i++) {
            int[] p = casePos.get(i);
            if (Math.abs(mx-p[0])<r && Math.abs(my-p[1])<r) {
                Case c = cases.get(i);
                hoveredInfo = "Case: "+c.getTitle()+" | Officer: "+c.getOfficerName()+" | "+c.getStatus();
                canvas.repaint(); return;
            }
        }
        for (int i=0; i<suspectPos.size(); i++) {
            int[] p = suspectPos.get(i);
            if (Math.abs(mx-p[0])<r && Math.abs(my-p[1])<r) {
                Suspect s = suspects.get(i);
                hoveredInfo = "Suspect: "+s.getName()+" | Age: "+s.getAge()+" | Danger: "+s.getDangerLevel();
                canvas.repaint(); return;
            }
        }
        for (int i=0; i<evidencePos.size(); i++) {
            int[] p = evidencePos.get(i);
            if (Math.abs(mx-p[0])<r && Math.abs(my-p[1])<r) {
                Evidence ev = evidences.get(i);
                hoveredInfo = "Evidence: "+ev.getTitle()+" | Type: "+ev.getType()+" | Found: "+ev.getFoundLocation();
                canvas.repaint(); return;
            }
        }
        canvas.repaint();
    }

    private Label legendLabel(String t, Color c) {
        Label l = new Label(t); l.setForeground(c);
        l.setFont(new Font("SansSerif", Font.BOLD, 11)); return l;
    }
}