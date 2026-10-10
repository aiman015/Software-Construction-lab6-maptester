import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;

public class Maptestergui extends JFrame {

    // Colours
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color SUCCESS = new Color(22, 163, 74);
    private static final Color ERROR   = new Color(220, 38, 38);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color GRAY    = new Color(229, 231, 235);
    private static final Color BG      = new Color(243, 244, 246);
    private static final Color TEXT    = new Color(31, 41, 55);
    private static final Color MUTED   = new Color(107, 114, 128);
    private static final Color VIOLET  = new Color(139, 92, 246);

    // --- Uses MapTester (capital T) ---
    private MapTester phoneBook = new MapTester();

    private JTextField nameField = new JTextField();
    private JTextField numberField = new JTextField();

    private DefaultListModel<String> listModel = new DefaultListModel<String>();
    private JList<String> contactList = new JList<String>(listModel);
    private JLabel countLabel = new JLabel("0");
    private JButton deleteButton;
    private CardLayout cardLayout = new CardLayout();
    private JPanel listCards;

    // Insight tiles
    private JLabel insTotal, insUnique, insShared, insLongest, insShortest, insMostShared;

    public Maptestergui() {
        setTitle("Phone Book");
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        add(createHeader(), BorderLayout.NORTH);
        add(createForm(), BorderLayout.CENTER);
        add(createSidebar(), BorderLayout.EAST);

        refreshContacts();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ---------- Header banner ----------
    private JPanel createHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Phone Book");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("HashMap demo: put() and get()");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(219, 234, 254));

        header.add(title);
        header.add(subtitle);
        return header;
    }

    // ---------- Form card + Centered Insights panel ----------
    private JPanel createForm() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(20, 25, 25, 25));

        // ============ TOP: The Form ============
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(4, 0, 4, 0);

        gbc.gridy = 0;
        card.add(createLabel("Name"), gbc);
        gbc.gridy = 1;
        styleField(nameField);
        card.add(nameField, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 4, 0);
        card.add(createLabel("Phone Number"), gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 0, 4, 0);
        styleField(numberField);
        card.add(numberField, gbc);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setBackground(Color.WHITE);
        JButton saveButton = createButton("Save", PRIMARY);
        JButton lookupButton = createButton("Lookup", SUCCESS);
        buttons.add(saveButton);
        buttons.add(lookupButton);

        gbc.gridy = 4;
        gbc.insets = new Insets(22, 0, 0, 0);
        card.add(buttons, gbc);

        saveButton.addActionListener(e -> saveEntry());
        lookupButton.addActionListener(e -> lookupEntry());
        nameField.addActionListener(e -> numberField.requestFocus());
        numberField.addActionListener(e -> saveEntry());

        // ============ BOTTOM: Insights Panel (CENTERED) ============
        JPanel insightsCard = new JPanel(new BorderLayout(0, 12));
        insightsCard.setBackground(Color.WHITE);
        insightsCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        // --- Centered title and subtitle ---
        JLabel title = new JLabel("Phone Book Insights", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Live statistics computed from the HashMap (values can repeat, keys cannot).",
                SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        // --- Centered stats grid ---
        JPanel stats = new JPanel(new GridLayout(2, 3, 12, 12));
        stats.setOpaque(false);

        insTotal      = createStatTile(stats, "Total Contacts", "0", PRIMARY);
        insUnique     = createStatTile(stats, "Unique Numbers", "0", SUCCESS);
        insShared     = createStatTile(stats, "Shared Numbers", "0", WARNING);
        insLongest    = createStatTile(stats, "Longest Name", "—", VIOLET);
        insShortest   = createStatTile(stats, "Shortest Number", "—", new Color(236, 72, 153));
        insMostShared = createStatTile(stats, "Most Shared", "—", new Color(14, 165, 233));

        // --- Centered buttons ---
        JButton demoBtn = createButton("Run HashMap Demo", VIOLET);
        demoBtn.setPreferredSize(new Dimension(180, 34));
        demoBtn.addActionListener(e -> runRawHashMapDemo());

        JButton refreshBtn = createButton("Refresh Insights", PRIMARY);
        refreshBtn.setPreferredSize(new Dimension(160, 34));
        refreshBtn.addActionListener(e -> updateInsights());

        JPanel bottomBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomBox.setOpaque(false);
        bottomBox.add(demoBtn);
        bottomBox.add(refreshBtn);

        // --- Assemble the insights card ---
        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        topBox.add(title);
        topBox.add(Box.createVerticalStrut(4));
        topBox.add(subtitle);
        topBox.add(Box.createVerticalStrut(16));
        topBox.add(stats);

        insightsCard.add(topBox, BorderLayout.CENTER);
        insightsCard.add(bottomBox, BorderLayout.SOUTH);

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setBackground(BG);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        insightsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        stack.add(card);
        stack.add(Box.createVerticalStrut(16));
        stack.add(insightsCard);

        wrapper.add(stack, BorderLayout.NORTH);
        return wrapper;
    }

    // ---------- Stat tile helper ----------
    private JLabel createStatTile(JPanel parent, String labelText, String valueText, Color color) {
        JPanel tile = new JPanel(new BorderLayout());
        tile.setBackground(new Color(249, 250, 251));
        tile.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(10, 12, 10, 12)));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(MUTED);

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 20));
        value.setForeground(color);

        tile.add(label, BorderLayout.NORTH);
        tile.add(value, BorderLayout.CENTER);

        parent.add(tile);
        return value;
    }

    // ---------- Raw HashMap Demo (bypasses GUI logic) ----------
    private void runRawHashMapDemo() {
        MapTester demo = new MapTester();

        // --- Duplicate Keys ---
        demo.enterNumber("Alice", "111-1111");
        demo.enterNumber("Alice", "222-2222"); // overwrites

        // --- Duplicate Values ---
        demo.enterNumber("Bob", "555-5555");
        demo.enterNumber("Charlie", "555-5555"); // allowed

        StringBuilder sb = new StringBuilder();
        sb.append("========== DUPLICATE KEYS ==========\n");
        sb.append("  put(\"Alice\", \"111-1111\")\n");
        sb.append("  put(\"Alice\", \"222-2222\")   <- same key\n");
        sb.append("  -> lookupNumber(\"Alice\") = ")
          .append(demo.lookupNumber("Alice")).append("\n");
        sb.append("  -> RESULT: 2nd value REPLACED the 1st.\n");
        sb.append("  -> Reason: HashMap keys must be unique.\n\n");

        sb.append("========= DUPLICATE VALUES =========\n");
        sb.append("  put(\"Bob\",     \"555-5555\")\n");
        sb.append("  put(\"Charlie\", \"555-5555\")   <- same value\n");
        sb.append("  -> Bob     = ").append(demo.lookupNumber("Bob")).append("\n");
        sb.append("  -> Charlie = ").append(demo.lookupNumber("Charlie")).append("\n");
        sb.append("  -> RESULT: BOTH entries exist.\n");
        sb.append("  -> Reason: HashMap allows duplicate VALUES.\n");

        JTextArea ta = new JTextArea(sb.toString(), 16, 46);
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setBackground(new Color(249, 250, 251));
        ta.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane sp = new JScrollPane(ta);

        JOptionPane.showMessageDialog(this, sp, "Raw HashMap Demo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- Compute and show live statistics ----------
    private void updateInsights() {
        if (insTotal == null) return;

        java.util.List<String> names = phoneBook.getNames();

        insTotal.setText(String.valueOf(names.size()));

        java.util.Set<String> distinctValues = new java.util.HashSet<String>();
        for (String key : names) {
            String val = phoneBook.lookupNumber(key);
            if (val != null) distinctValues.add(val);
        }
        insUnique.setText(String.valueOf(distinctValues.size()));

        java.util.Map<String, Integer> valueCount = new java.util.HashMap<String, Integer>();
        for (String key : names) {
            String val = phoneBook.lookupNumber(key);
            if (val == null) continue;
            Integer c = valueCount.get(val);
            valueCount.put(val, c == null ? 1 : c + 1);
        }
        int sharedCount = 0;
        String topNumber = null;
        int topCount = 0;
        for (java.util.Map.Entry<String, Integer> e : valueCount.entrySet()) {
            if (e.getValue() > 1) sharedCount++;
            if (e.getValue() > topCount) {
                topCount = e.getValue();
                topNumber = e.getKey();
            }
        }
        insShared.setText(String.valueOf(sharedCount));

        String longest = "—";
        int longestLen = 0;
        for (String key : names) {
            String base = baseName(key);
            if (base.length() > longestLen) {
                longestLen = base.length();
                longest = base;
            }
        }
        insLongest.setText(longest);

        String shortest = "—";
        int shortestLen = Integer.MAX_VALUE;
        for (String key : names) {
            String val = phoneBook.lookupNumber(key);
            if (val != null && val.length() < shortestLen) {
                shortestLen = val.length();
                shortest = val;
            }
        }
        insShortest.setText(shortest);

        if (topCount > 1 && topNumber != null) {
            insMostShared.setText(topNumber + " (" + topCount + ")");
        } else {
            insMostShared.setText("—");
        }
    }

    // ---------- Sidebar ----------
    private JPanel createSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(Color.WHITE);
        side.setBorder(new MatteBorder(0, 1, 0, 0, new Color(229, 231, 235)));
        side.setPreferredSize(new Dimension(320, 400));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(18, 18, 12, 18));
        JLabel title = new JLabel("Saved Contacts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(Color.WHITE);
        countLabel.setOpaque(true);
        countLabel.setBackground(PRIMARY);
        countLabel.setBorder(new EmptyBorder(3, 10, 3, 10));
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pill.setOpaque(false);
        pill.add(countLabel);
        top.add(title, BorderLayout.WEST);
        top.add(pill, BorderLayout.EAST);

        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.setFixedCellHeight(66);
        contactList.setCellRenderer(new ListCellRenderer<String>() {
            private JPanel row = new JPanel(new BorderLayout(12, 0));
            private Avatar avatar = new Avatar();
            private JPanel texts = new JPanel(new GridLayout(2, 1));
            private JLabel nameLabel = new JLabel();
            private JLabel numberLabel = new JLabel();

            {
                row.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 1, 0, new Color(243, 244, 246)),
                        new EmptyBorder(8, 14, 8, 14)));
                texts.setOpaque(false);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                numberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                numberLabel.setForeground(MUTED);
                texts.add(nameLabel);
                texts.add(numberLabel);
                row.add(avatar, BorderLayout.WEST);
                row.add(texts, BorderLayout.CENTER);
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String name,
                    int index, boolean selected, boolean focus) {
                String number = phoneBook.lookupNumber(name);
                nameLabel.setText(name);
                numberLabel.setText(number == null ? "" : number);
                avatar.set(name, selected);
                row.setBackground(selected ? new Color(239, 246, 255) : Color.WHITE);
                nameLabel.setForeground(selected ? new Color(30, 58, 138) : TEXT);
                return row;
            }
        });
        contactList.addListSelectionListener(e -> updateDeleteButton());
        contactList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteSelectedContact();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(contactList);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        JLabel empty = new JLabel("<html><div style='text-align:center'>No contacts saved yet.<br>"
                + "Add one using the form.</div></html>", SwingConstants.CENTER);
        empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        empty.setForeground(MUTED);
        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setBackground(Color.WHITE);
        emptyPanel.add(empty, BorderLayout.CENTER);

        listCards = new JPanel(cardLayout);
        listCards.setBackground(Color.WHITE);
        listCards.setBorder(new LineBorder(new Color(229, 231, 235), 1, true));
        listCards.add(scroll, "list");
        listCards.add(emptyPanel, "empty");

        JPanel listWrap = new JPanel(new BorderLayout());
        listWrap.setBackground(Color.WHITE);
        listWrap.setBorder(new EmptyBorder(0, 18, 0, 18));
        listWrap.add(listCards, BorderLayout.CENTER);

        deleteButton = createButton("Delete Contact", ERROR);
        deleteButton.addActionListener(e -> deleteSelectedContact());
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(new EmptyBorder(12, 18, 18, 18));
        bottom.add(deleteButton, BorderLayout.CENTER);

        side.add(top, BorderLayout.NORTH);
        side.add(listWrap, BorderLayout.CENTER);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private void refreshContacts() {
        listModel.clear();
        for (String key : phoneBook.getNames()) {
            listModel.addElement(key);
        }
        countLabel.setText(String.valueOf(listModel.size()));
        cardLayout.show(listCards, listModel.isEmpty() ? "empty" : "list");
        updateDeleteButton();
        updateInsights();
    }

    private void updateDeleteButton() {
        boolean selected = contactList.getSelectedIndex() >= 0;
        deleteButton.setEnabled(selected);
        deleteButton.setBackground(selected ? ERROR : new Color(209, 213, 219));
    }

    private void deleteSelectedContact() {
        String name = contactList.getSelectedValue();
        if (name == null) return;
        String number = phoneBook.lookupNumber(name);
        boolean sure = confirm("Delete Contact",
                "Delete \"" + name + "\" (" + number + ") from the phone book?\n\n"
              + "This cannot be undone.",
                ERROR, "No, keep it", "Yes, delete");
        if (!sure) return;
        phoneBook.deleteContact(name);
        refreshContacts();
    }

    // ---------- Save entry (pure HashMap behavior: put overwrites) ----------
    private void saveEntry() {
        String name = nameField.getText().trim();
        String number = numberField.getText().trim();

        if (name.isEmpty() || number.isEmpty()) {
            showMessage("Missing Information",
                    "Please fill in both the name and the phone number.", WARNING);
            return;
        }

        number = number.replaceFirst("^-+", "").trim();

        String numberError = validateNumber(number);
        if (numberError != null) {
            showMessage("Invalid Phone Number", numberError, ERROR);
            return;
        }

        if (!name.matches("[A-Za-z][A-Za-z .'-]*")) {
            boolean sure = confirm("Unusual Name",
                    "\"" + name + "\" does not look like a valid name (it contains digits or symbols).\n\n"
                  + "Are you sure you want to save it?",
                    WARNING, "No, go back", "Yes, save it");
            if (!sure) return;
        }

        // ---- PURE HASHMAP BEHAVIOR ----
        String existing = phoneBook.lookupNumber(name);
        if (existing != null) {
            boolean replace = confirm("Already Exists",
                    "\"" + name + "\" is already saved as " + existing + ".\n\n"
                  + "Do you want to replace it with " + number + "?",
                    WARNING, "No, cancel", "Yes, replace");
            if (!replace) return;
        }

        phoneBook.enterNumber(name, number);

        refreshContacts();
        contactList.setSelectedValue(name, true);

        nameField.setText("");
        numberField.setText("");
        nameField.requestFocus();
    }

    // Since we no longer append "(2)" suffixes, baseName just returns the key.
    private static String baseName(String key) {
        return key;
    }

    private String validateNumber(String number) {
        if (number.contains(".")) return "A phone number cannot be a decimal.";
        if (!number.matches("\\+?\\d+")) {
            return "A phone number must contain digits only (an optional + is allowed at the start).";
        }
        String digits = number.replace("+", "");
        if (digits.matches("0+")) return "A phone number cannot be all zeros.";
        if (digits.length() < 7 || digits.length() > 15) {
            return "A phone number must be 7 to 15 digits long.";
        }
        return null;
    }

    private void lookupEntry() {
        StyledDialog choice = new StyledDialog(this, "Lookup",
                "How would you like to search?", PRIMARY, "?", false,
                new String[]{"By Name", "By Phone Number", "Cancel"},
                new Color[]{PRIMARY, SUCCESS, GRAY});
        choice.showDialog();

        if (choice.result == 0) lookupByName();
        else if (choice.result == 1) lookupByNumber();
    }

    private void lookupByName() {
        StyledDialog ask = new StyledDialog(this, "Search by Name",
                "Type the name you want to search for:", PRIMARY, "?", true,
                new String[]{"Search", "Cancel"}, new Color[]{PRIMARY, GRAY});
        ask.showDialog();
        if (ask.result != 0) return;

        String name = ask.input.trim();
        if (name.isEmpty()) {
            showMessage("Nothing Typed", "Please type a name to search.", WARNING);
            return;
        }

        java.util.List<String> matches = new java.util.ArrayList<String>();
        for (String key : phoneBook.getNames()) {
            if (key.equalsIgnoreCase(name)) matches.add(key);
        }
        if (matches.isEmpty()) {
            showMessage("No Entry Found", "No contact named \"" + name + "\" was found.", WARNING);
        } else {
            showMatches(matches);
        }
    }

    private void lookupByNumber() {
        StyledDialog ask = new StyledDialog(this, "Search by Phone Number",
                "Type the phone number you want to search for:", SUCCESS, "?", true,
                new String[]{"Search", "Cancel"}, new Color[]{SUCCESS, GRAY});
        ask.showDialog();
        if (ask.result != 0) return;

        String number = ask.input.trim();
        if (number.isEmpty()) {
            showMessage("Nothing Typed", "Please type a phone number to search.", WARNING);
            return;
        }

        java.util.List<String> matches = new java.util.ArrayList<String>();
        for (String key : phoneBook.getNames()) {
            if (number.equals(phoneBook.lookupNumber(key))) matches.add(key);
        }
        if (matches.isEmpty()) {
            showMessage("No Entry Found", "No contact with the number " + number + " was found.", WARNING);
        } else {
            showMatches(matches);
        }
    }

    private void showMatches(java.util.List<String> matches) {
        refreshContacts();
        String first = matches.get(0);
        nameField.setText(first);
        numberField.setText(phoneBook.lookupNumber(first));
        contactList.setSelectedValue(first, true);
    }

    private void showMessage(String title, String message, Color accent) {
        new StyledDialog(this, title, message, accent, "!", false,
                new String[]{"Got it"}, new Color[]{accent}).showDialog();
    }

    private boolean confirm(String title, String message, Color accent, String noText, String yesText) {
        StyledDialog d = new StyledDialog(this, title, message, accent, "!", false,
                new String[]{noText, yesText}, new Color[]{GRAY, accent});
        d.showDialog();
        return d.result == 1;
    }

    private static String toHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);
        return label;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(300, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(4, 10, 4, 10)));
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(100, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // =====================================================================
    //  Custom pop-up dialog
    // =====================================================================
    private static class StyledDialog extends JDialog {
        int result = -1;
        String input = null;
        private JTextField inputField;

        StyledDialog(Frame owner, String title, String message, Color accent, String symbol,
                     boolean withInput, String[] labels, Color[] colors) {
            super(owner, true);
            setUndecorated(true);
            getRootPane().setBorder(new LineBorder(new Color(209, 213, 219), 1));

            JPanel content = new JPanel(new BorderLayout());
            content.setBackground(Color.WHITE);
            setContentPane(content);

            JPanel header = new JPanel(new BorderLayout(14, 0));
            header.setBackground(accent);
            header.setBorder(new EmptyBorder(16, 22, 16, 22));
            header.add(new IconBadge(symbol, accent), BorderLayout.WEST);
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            header.add(titleLabel, BorderLayout.CENTER);

            JPanel body = new JPanel();
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBackground(Color.WHITE);
            body.setBorder(new EmptyBorder(20, 24, 6, 24));

            JLabel msg = new JLabel("<html><div style='width:330px'>" + toHtml(message) + "</div></html>");
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            msg.setForeground(TEXT);
            msg.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(msg);

            if (withInput) {
                inputField = new JTextField();
                inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                inputField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(209, 213, 219), 1, true),
                        new EmptyBorder(4, 10, 4, 10)));
                inputField.setPreferredSize(new Dimension(330, 38));
                inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
                inputField.setAlignmentX(Component.LEFT_ALIGNMENT);
                body.add(Box.createVerticalStrut(14));
                body.add(inputField);
            }

            JPanel buttons = new JPanel(new GridLayout(1, labels.length, 10, 0));
            buttons.setBackground(Color.WHITE);
            buttons.setBorder(new EmptyBorder(16, 24, 22, 24));
            JButton firstButton = null;
            for (int i = 0; i < labels.length; i++) {
                final int index = i;
                JButton b = makeButton(labels[i], colors[i]);
                b.addActionListener(e -> {
                    result = index;
                    if (inputField != null) input = inputField.getText();
                    dispose();
                });
                buttons.add(b);
                if (i == 0) firstButton = b;
            }

            content.add(header, BorderLayout.NORTH);
            content.add(body, BorderLayout.CENTER);
            content.add(buttons, BorderLayout.SOUTH);

            getRootPane().setDefaultButton(firstButton);
            getRootPane().registerKeyboardAction(e -> {
                result = -1;
                dispose();
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    if (inputField != null) inputField.requestFocusInWindow();
                }
            });

            pack();
            setLocationRelativeTo(owner);
        }

        void showDialog() { setVisible(true); }

        private static JButton makeButton(String text, Color base) {
            boolean light = (base.getRed() + base.getGreen() + base.getBlue()) / 3 > 200;
            Color hover = light ? new Color(209, 213, 219) : base.darker();

            JButton b = new JButton(text);
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setBackground(base);
            b.setForeground(light ? TEXT : Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setOpaque(true);
            b.setPreferredSize(new Dimension(130, 42));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
                public void mouseExited(MouseEvent e) { b.setBackground(base); }
            });
            return b;
        }
    }

    private static class Avatar extends JComponent {
        private String letter = "?";
        private boolean selected;

        Avatar() { setPreferredSize(new Dimension(40, 40)); }

        void set(String name, boolean selected) {
            this.letter = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();
            this.selected = selected;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(selected ? PRIMARY : new Color(219, 234, 254));
            g2.fillOval(0, 0, 38, 38);
            g2.setColor(selected ? Color.WHITE : new Color(30, 64, 175));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            int x = (38 - fm.stringWidth(letter)) / 2;
            int y = (38 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(letter, x, y);
            g2.dispose();
        }
    }

    private static class IconBadge extends JComponent {
        private final String symbol;
        private final Color color;

        IconBadge(String symbol, Color color) {
            this.symbol = symbol;
            this.color = color;
            setPreferredSize(new Dimension(44, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillOval(2, 2, 40, 40);
            g2.setColor(color);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(symbol)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(symbol, x, y);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new Maptestergui());
    }
}