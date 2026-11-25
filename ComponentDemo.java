
import java.awt.*;
import javax.swing.*;

public class ComponentDemo extends JFrame {

    public ComponentDemo() {
        setTitle("Swing Component Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel nameLabel = new JLabel("Nama:");
        JTextField nameField = new JTextField(15);

        JLabel deptLabel = new JLabel("Jurusan:");
        JComboBox<String> deptCombo = new JComboBox<>(
            new String[]{"Informatika", "Elektronika", "Mesin"}
        );

        JCheckBox activeCheck = new JCheckBox(
            "Mahasiswa aktif", true
        );

        JRadioButton regulerRadio = new JRadioButton("Reguler", true);
        JRadioButton ekstensiRadio = new JRadioButton("Ekstensi");

        ButtonGroup group = new ButtonGroup();
        group.add(regulerRadio);
        group.add(ekstensiRadio);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(regulerRadio);
        radioPanel.add(ekstensiRadio);

        JButton button = new JButton("Tampilkan");

        JTextArea outputArea = new JTextArea(6, 25);
        outputArea.setEditable(false);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        formPanel.add(nameLabel);
        formPanel.add(nameField);

        formPanel.add(deptLabel);
        formPanel.add(deptCombo);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(activeCheck);

        formPanel.add(new JLabel("Jenis kelas:"));
        formPanel.add(radioPanel);

        formPanel.add(new JLabel(""));
        formPanel.add(button);

        button.addActionListener(e -> {

            String nama = nameField.getText().trim();

            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Nama tidak boleh kosong!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE
                );

                nameField.requestFocus();
                return;
            }

            String jurusan = (String) deptCombo.getSelectedItem();

            String status = activeCheck.isSelected()
                ? "Aktif" : "Tidak aktif";

            String kelas = regulerRadio.isSelected()
                ? "Reguler" : "Ekstensi";

            outputArea.setText(
                "Nama: " + nama +
                "\nJurusan: " + jurusan +
                "\nStatus: " + status +
                "\nJenis kelas: " + kelas
            );
        });


        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ComponentDemo::new);
    }
}
