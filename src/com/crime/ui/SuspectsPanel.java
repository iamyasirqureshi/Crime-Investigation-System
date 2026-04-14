package com.crime.ui;

import com.crime.dao.CaseDAO;
import com.crime.dao.SuspectDAO;
import com.crime.model.Case;
import com.crime.model.Suspect;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SuspectsPanel extends Panel {

    private MainFrame parent;
    private SuspectDAO suspectDAO = new SuspectDAO();
    private CaseDAO caseDAO       = new CaseDAO();
    private java.awt.List suspectList;
    private List<Suspect> suspects;

    private static final Color BG      = new Color(15, 20, 35);
    private static final Color CARD_BG = new Color(25, 35, 60);

    public SuspectsPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        Panel header = new Panel(new BorderLayout());
        header.setBackground(BG);
        Label title = new Label("  SUSPECTS DATABASE", Label.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        btnPanel.setBackground(BG);
        Button addBtn    = makeBtn("Add Suspect", new Color(60, 180, 100));
        Button deleteBtn = makeBtn("Delete",       new Color(200, 60, 60));
        Button refreshBtn= makeBtn("Refresh",      new Color(70, 80, 110));
        addBtn.addActionListener(e    -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e-> loadData());
        btnPanel.add(addBtn); btnPanel.add(deleteBtn); btnPanel.add(refreshBtn);
        header.add(btnPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        suspectList = new java.awt.List(22);
        suspectList.setBackground(CARD_BG);
        suspectList.setForeground(new Color(200, 210, 255));
        suspectList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(suspectList, BorderLayout.CENTER);

        Panel legend = new Panel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        legend.setBackground(BG);
        legend.add(makeLabel("LOW DANGER",    new Color(80, 200, 100)));
        legend.add(makeLabel("MEDIUM DANGER", new Color(255, 200, 60)));
        legend.add(makeLabel("HIGH DANGER",   new Color(220, 80, 80)));
        add(legend, BorderLayout.SOUTH);
        loadData();
    }

    public void loadData() {
        suspectList.removeAll();
        suspects = suspectDAO.getAllSuspects();
        for (Suspect s : suspects)
            suspectList.add(String.format("  [%-6s]  %-22s  Age:%-4d  %-8s  Case:%-15s  %s",
                    s.getDangerLevel(), s.getName(), s.getAge(), s.getGender(),
                    s.getCaseNumber() != null ? s.getCaseNumber() : "N/A", s.getAddress()));
    }

    private void showAddDialog() {
        Dialog dialog = new Dialog(getFrame(), "Add Suspect", true);
        dialog.setLayout(new GridLayout(8, 2, 8, 8));
        dialog.setBackground(new Color(25, 35, 60));
        dialog.setSize(500, 400); dialog.setLocationRelativeTo(this);

        Font lf = new Font("SansSerif", Font.BOLD, 12);
        Color lc = new Color(180, 190, 230);

        TextField tfName = makeField();
        TextField tfAge  = makeField();
        TextField tfAddr = makeField();
        TextArea taBg    = new TextArea(3, 30);
        taBg.setBackground(new Color(18, 26, 48)); taBg.setForeground(Color.WHITE);

        Choice genderChoice = new Choice();
        genderChoice.add("Male"); genderChoice.add("Female"); genderChoice.add("Other");
        genderChoice.setBackground(new Color(18, 26, 48)); genderChoice.setForeground(Color.WHITE);

        Choice dangerChoice = new Choice();
        dangerChoice.add("LOW"); dangerChoice.add("MEDIUM"); dangerChoice.add("HIGH");
        dangerChoice.setBackground(new Color(18, 26, 48)); dangerChoice.setForeground(Color.WHITE);

        Choice caseChoice = new Choice();
        caseChoice.setBackground(new Color(18, 26, 48)); caseChoice.setForeground(Color.WHITE);
        List<Case> cases = caseDAO.getAllCases();
        for (Case c : cases) caseChoice.add(c.getCaseNumber() + " - " + c.getTitle());

        addRow(dialog, "Full Name:",    tfName,      lf, lc);
        addRow(dialog, "Age:",          tfAge,       lf, lc);
        addRow(dialog, "Gender:",       genderChoice,lf, lc);
        addRow(dialog, "Address:",      tfAddr,      lf, lc);
        addRow(dialog, "Danger Level:", dangerChoice,lf, lc);
        addRow(dialog, "Linked Case:",  caseChoice,  lf, lc);
        Label bl = new Label("Background:"); bl.setFont(lf); bl.setForeground(lc);
        dialog.add(bl); dialog.add(taBg);

        Button save   = makeBtn("Save",   new Color(60, 180, 100));
        Button cancel = makeBtn("Cancel", new Color(100, 50, 50));
        save.addActionListener(e -> {
            try {
                Suspect s = new Suspect();
                s.setName(tfName.getText().trim());
                s.setAge(Integer.parseInt(tfAge.getText().trim()));
                s.setGender(genderChoice.getSelectedItem());
                s.setAddress(tfAddr.getText().trim());
                s.setBackground(taBg.getText().trim());
                s.setDangerLevel(dangerChoice.getSelectedItem());
                s.setCaseId(cases.get(caseChoice.getSelectedIndex()).getCaseId());
                if (suspectDAO.addSuspect(s)) { showMsg("Suspect added!"); loadData(); dialog.dispose(); }
                else showMsg("Failed.");
            } catch (Exception ex) { showMsg("Error: " + ex.getMessage()); }
        });
        cancel.addActionListener(e -> dialog.dispose());
        dialog.add(save); dialog.add(cancel);
        dialog.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { dialog.dispose(); } });
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int idx = suspectList.getSelectedIndex();
        if (idx < 0) { showMsg("Select a suspect first."); return; }
        suspectDAO.deleteSuspect(suspects.get(idx).getSuspectId());
        showMsg("Deleted!"); loadData();
    }

    private Frame getFrame() {
        Container c = getParent();
        while (c != null && !(c instanceof Frame)) c = c.getParent();
        return (Frame) c;
    }
    private TextField makeField() {
        TextField tf = new TextField();
        tf.setBackground(new Color(18, 26, 48)); tf.setForeground(Color.WHITE); return tf;
    }
    private Button makeBtn(String t, Color bg) {
        Button b = new Button(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    private Label makeLabel(String t, Color c) {
        Label l = new Label(t); l.setForeground(c);
        l.setFont(new Font("SansSerif", Font.BOLD, 12)); return l;
    }
    private void addRow(Dialog d, String lt, Component f, Font lf, Color lc) {
        Label l = new Label(lt); l.setFont(lf); l.setForeground(lc); d.add(l); d.add(f);
    }
    private void showMsg(String msg) {
        Dialog d = new Dialog(getFrame(), "Info", true);
        d.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        d.setBackground(new Color(25, 35, 60)); d.setSize(350, 130); d.setLocationRelativeTo(this);
        Label l = new Label(msg); l.setForeground(Color.WHITE);
        Button ok = makeBtn("OK", new Color(60, 130, 220));
        ok.addActionListener(e -> d.dispose()); d.add(l); d.add(ok);
        d.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { d.dispose(); } });
        d.setVisible(true);
    }
}