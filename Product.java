
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Product {
    private final String id;
    private final String name;
    private final int price;

    public Product(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            List<Product> products = new ArrayList<>();

            products.add(new Product("P001", "Air Mineral 600ml", 3000));
            products.add(new Product("P002", "Kopi Sachet", 5000));
            products.add(new Product("P003", "Roti isi", 8000));
            products.add(new Product("P004", "Snack Keripik", 6000));
            products.add(new Product("P005", "Minuman Botol", 12000));

            DefaultTableModel productModel =
                new DefaultTableModel(
                    new Object[]{"ID", "Nama Produk", "Harga (Rp)"}, 0
                ) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

            for (Product p : products) {
                productModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getPrice()
                });
            }

            JTable productTable = new JTable(productModel);

            JLabel selectedLabel =
                new JLabel("Belum ada produk dipilih");

            productTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (e.getValueIsAdjusting()) return;

                    int row = productTable.getSelectedRow();

                    if (row >= 0) {
                        Product selected = products.get(row);

                        selectedLabel.setText(
                            "Dipilih: " + selected.getId() +
                            " - " + selected.getName() +
                            " (Rp" + selected.getPrice() + ")"
                        );
                    }
                });

            JFrame frame = new JFrame("Daftar Produk");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(10, 10));

            frame.add(
                new JScrollPane(productTable),
                BorderLayout.CENTER
            );

            frame.add(selectedLabel, BorderLayout.SOUTH);

            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
