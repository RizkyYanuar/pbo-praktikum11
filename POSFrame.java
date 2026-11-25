import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class POSFrame extends JFrame {

    private final List<Product> products = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();

    private JTable productTable;
    private JTable cartTable;

    private JTextField qtyField;
    private JLabel selectedLabel;

    private JLabel totalLabel;
    private JLabel pointsLabel;
    private JTextArea receiptArea;

    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;

    private JButton addButton;
    private JButton checkoutButton;
    private JButton printButton;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public POSFrame() {
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        setTitle("POIN Off-Sales - Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        initProductData();
        initComponents();
        initTables();
        buildLayout();
        bindEvents();
        createMenuBar();

        setSize(1000, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initProductData() {
        products.add(new Product("P001", "Air Mineral 600ml", 3000));
        products.add(new Product("P002", "Kopi Sachet", 5000));
        products.add(new Product("P003", "Roti isi", 8000));
        products.add(new Product("P004", "Snack Keripik", 6000));
        products.add(new Product("P005", "Minuman Botol", 12000));
    }

    private void initComponents() {
        selectedLabel = new JLabel("Pilih produk dari tabel di kiri");

        qtyField = new JTextField("1", 5);

        totalLabel = new JLabel("Total: Rp0,00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        pointsLabel = new JLabel("Points: 0");

        receiptArea = new JTextArea(8, 30);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void bindEvents() {

        productTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    int row = productTable.getSelectedRow();

                    if (row >= 0) {

                        Product selected = products.get(
                                productTable.convertRowIndexToModel(row));

                        selectedLabel.setText(
                                "Dipilih: " + selected.getId()
                                        + " - " + selected.getName()
                                        + " (Rp" + selected.getPrice() + ",00)");
                    }
                });

        addButton.addActionListener(
                e -> addSelectedProductToCart());

        checkoutButton.addActionListener(
                e -> checkout());

        printButton.addActionListener(
                e -> printReceipt());
    }

    private void addSelectedProductToCart() {

        int row = productTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Silakan pilih produk terlebih dahulu!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qty;

        try {
            String input = qtyField.getText().trim();

            if (input.isEmpty()) {
                throw new NumberFormatException();
            }

            qty = Integer.parseInt(input);

            if (qty <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Qty harus lebih dari 0!",
                        "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                qtyField.requestFocus();
                return;
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Qty harus berupa bilangan bulat yang valid!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);

            qtyField.requestFocus();
            qtyField.selectAll();
            return;
        }

        Product selected = products.get(
                productTable.convertRowIndexToModel(row));

        CartItem item = new CartItem(selected, qty);

        cart.add(item);

        refreshCartTable();

        qtyField.setText("1");
    }

    private void refreshCartTable() {

        cartModel.setRowCount(0);

        for (CartItem item : cart) {

            Product p = item.getProduct();

            cartModel.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    item.getQty(),
                    p.getPrice(),
                    item.getSubtotal()
            });
        }

        updateSummary();
    }

    private void updateSummary() {

        int total = 0;

        for (CartItem item : cart) {
            total += item.getSubtotal();
        }

        int points = total / 1000;

        totalLabel.setText(
                "Total: " + currencyFormat.format(total));

        pointsLabel.setText(
                "Points: " + points);
    }

    private void checkout() {

        if (cart.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Keranjang masih kosong!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        StringBuilder receipt = new StringBuilder();

        receipt.append("================================\n");
        receipt.append("          POIN OFF-SALES        \n");
        receipt.append("================================\n\n");

        receipt.append("Daftar Pembelian:\n\n");

        int total = 0;

        for (CartItem item : cart) {

            Product p = item.getProduct();

            receipt.append(p.getName()).append("\n");

            receipt.append("Harga : ")
                    .append(currencyFormat.format(p.getPrice()))
                    .append("\n");

            receipt.append("Qty   : ")
                    .append(item.getQty())
                    .append("\n");

            receipt.append("Subtotal: ")
                    .append(currencyFormat.format(item.getSubtotal()))
                    .append("\n\n");

            total += item.getSubtotal();
        }

        int points = total / 1000;

        receipt.append("--------------------------------\n");

        receipt.append("TOTAL  : ")
                .append(currencyFormat.format(total))
                .append("\n");

        receipt.append("POINTS : ")
                .append(points)
                .append("\n");

        receipt.append("--------------------------------\n\n");

        receipt.append("Terima kasih telah berbelanja!\n");
        receipt.append("       Sampai jumpa kembali.\n");

        receipt.append("================================\n");

        receiptArea.setText(receipt.toString());

        cart.clear();

        refreshCartTable();

        JOptionPane.showMessageDialog(
                this,
                "Checkout berhasil!\nTerima kasih telah berbelanja.",
                "Transaksi Berhasil",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void printReceipt() {

        try {

            boolean complete = receiptArea.print();

            if (complete) {
                JOptionPane.showMessageDialog(
                        this,
                        "Struk berhasil dikirim ke printer!",
                        "Print",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (java.awt.print.PrinterException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal mencetak struk: " + ex.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildLayout() {

        JPanel productPanel = new JPanel(new BorderLayout(5, 5));
        productPanel.setBorder(
                new TitledBorder("Produk"));

        productPanel.add(
                new JScrollPane(productTable),
                BorderLayout.CENTER);

        JPanel productBottom = new JPanel();
        productBottom.setLayout(
                new BoxLayout(productBottom, BoxLayout.Y_AXIS));

        productBottom.add(selectedLabel);

        JPanel inputPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        inputPanel.add(new JLabel("Qty:"));
        inputPanel.add(qtyField);

        addButton = new JButton("Add to Cart");
        inputPanel.add(addButton);

        productBottom.add(inputPanel);

        productPanel.add(productBottom, BorderLayout.SOUTH);

        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setBorder(
                new TitledBorder("Keranjang"));

        cartPanel.add(
                new JScrollPane(cartTable),
                BorderLayout.CENTER);

        JPanel cartBottom = new JPanel(new BorderLayout(5, 5));

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(
                new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));

        summaryPanel.add(totalLabel);
        summaryPanel.add(Box.createVerticalStrut(5));
        summaryPanel.add(pointsLabel);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        checkoutButton = new JButton("Checkout");
        printButton = new JButton("Cetak");

        buttonPanel.add(checkoutButton);
        buttonPanel.add(printButton);

        summaryPanel.add(buttonPanel);

        cartBottom.add(summaryPanel, BorderLayout.NORTH);

        JPanel receiptPanel = new JPanel(new BorderLayout(3, 3));
        receiptPanel.add(new JLabel("Struk:"), BorderLayout.NORTH);
        receiptPanel.add(
                new JScrollPane(receiptArea),
                BorderLayout.CENTER);

        cartBottom.add(receiptPanel, BorderLayout.CENTER);

        cartPanel.add(cartBottom, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(
                new GridLayout(1, 2, 8, 0));

        mainPanel.add(productPanel);
        mainPanel.add(cartPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(e -> {
            System.exit(0);
        });

        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    this,
                    "POIN Off-Sales\n"
                            + "Aplikasi Point of Sales Sederhana\n"
                            + "Dibuat menggunakan Java Swing.",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);

        });

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initTables() {

        productModel = new DefaultTableModel(
                new Object[] { "ID", "Nama Produk", "Harga (Rp)" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Product p : products) {
            productModel.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    p.getPrice()
            });
        }

        productTable = new JTable(productModel);
        productTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        cartModel = new DefaultTableModel(
                new Object[] { "ID", "Nama Produk", "Qty", "Harga", "Subtotal" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);
    }

}
