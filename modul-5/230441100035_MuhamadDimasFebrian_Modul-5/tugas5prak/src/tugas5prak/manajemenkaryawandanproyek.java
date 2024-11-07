/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package tugas5prak;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author ASUS A455LN
 */
public class manajemenkaryawandanproyek extends javax.swing.JFrame {
    Connection conn;
    private DefaultTableModel modelKaryawan;
    private DefaultTableModel modelProyek;
    private DefaultTableModel modelTransaksi;
    
    public manajemenkaryawandanproyek() {
        initComponents();
        conn = koneksi.getConnection();
        
        initTableModels();
        loadDataKaryawan();
        loadDataProyek();
        loadDataTransaksi();
        loadComboBoxKaryawan();
        loadComboBoxProyek();
        clearTransaksiFields();
       

        clearTransaksiFields();
        
    }
    
    private void initTableModels() {
        modelKaryawan = new DefaultTableModel(new String[] { "ID", "Nama", "Jabatan", "Departemen" }, 0);
        tbl_karyawan.setModel(modelKaryawan);

        modelProyek = new DefaultTableModel(new String[] { "ID", "Nama Proyek", "Pengerjaan Mingguan" }, 0);
        tbl_proyek.setModel(modelProyek);

        modelTransaksi = new DefaultTableModel(new String[] { "ID Karyawan", "ID Proyek", "Nama Karyawan", "Nama Proyek", "Durasi" }, 0);
        tbl_transaksi.setModel(modelTransaksi);
    }
    
//    menampilkan ke table
    private void loadDataKaryawan() {
        modelKaryawan.setRowCount(0);
        try {
            String sql = "SELECT * FROM tb_karyawan";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelKaryawan.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("departemen")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error Load Data Karyawan: " + e.getMessage());
        }
    }
    
    private void loadDataProyek() {
        modelProyek.setRowCount(0);
        try {
            String sql = "SELECT * FROM tb_proyek";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelProyek.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama_proyek"),
                    String.format("%d Minggu", rs.getInt("durasi_pengerjaan"))
                });
            }
        } catch (SQLException e) {
            System.out.println("Error Load Data Proyek: " + e.getMessage());
        }
    }
    
    private void loadDataTransaksi() {
        modelTransaksi.setRowCount(0);
        try {
            String sql = "SELECT t.id_karyawan, t.id_proyek, t.durasi, " +
                         "k.nama AS nama_karyawan, p.nama_proyek AS nama_proyek, " +
                         "p.durasi_pengerjaan " +
                         "FROM tb_transaksi t " +
                         "JOIN tb_karyawan k ON t.id_karyawan = k.id " +
                         "JOIN tb_proyek p ON t.id_proyek = p.id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelTransaksi.addRow(new Object[]{
                    rs.getInt("id_karyawan"),
                    rs.getInt("id_proyek"),
                    rs.getString("nama_karyawan"),
                    rs.getString("nama_proyek"),
                    String.format("%d Minggu", rs.getInt("durasi"))
                });
            }
        } catch (SQLException e) {
            System.out.println("Error Load Data Transaksi: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi: " + e.getMessage());
        }
    }
    
//    CRUD tab karyawan
    
    private void tambahKaryawan() {
        if (tf_namakaryawan.getText().trim().isEmpty() || 
        tf_jabatankaryawan.getText().trim().isEmpty() || 
        tf_deparkaryawan.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return; // Hentikan eksekusi jika ada field yang kosong
    }
        PreparedStatement ps = null;
        try {
        String sql = "INSERT INTO tb_karyawan (nama, jabatan, departemen) VALUES (?, ?, ?)";
        ps = conn.prepareStatement(sql);
        ps.setString(1, tf_namakaryawan.getText().trim());
        ps.setString(2, tf_jabatankaryawan.getText().trim());
        ps.setString(3, tf_deparkaryawan.getText().trim());

        int affectedRows = ps.executeUpdate(); 
        if (affectedRows > 0) {
            JOptionPane.showMessageDialog(this, "Data Karyawan berhasil ditambahkan.");
            loadDataKaryawan();
            clearKaryawanFields();
            loadComboBoxKaryawan();
            clearTransaksiFields();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data karyawan.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Kesalahan saat menambah Data Karyawan: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
    } 

}
    
    private void updateKaryawan() {
    if (id_karyawan.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "ID Karyawan harus diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return; 
    }

    int id;
    try {
        id = Integer.parseInt(id_karyawan.getText().trim()); 
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "ID Karyawan tidak valid.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        return; 
    }

    
    if (tf_namakaryawan.getText().trim().isEmpty() || 
        tf_jabatankaryawan.getText().trim().isEmpty() || 
        tf_deparkaryawan.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return; 
    }

    PreparedStatement ps = null; 
    try {
        String sql = "UPDATE tb_karyawan SET nama = ?, jabatan = ?, departemen = ? WHERE id = ?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, tf_namakaryawan.getText().trim());
        ps.setString(2, tf_jabatankaryawan.getText().trim());
        ps.setString(3, tf_deparkaryawan.getText().trim());
        ps.setInt(4, id);

        int affectedRows = ps.executeUpdate(); 
        if (affectedRows > 0) {
            JOptionPane.showMessageDialog(this, "Data Karyawan berhasil diupdate.");
            loadDataKaryawan();
            clearKaryawanFields();
            loadComboBoxKaryawan();
            clearTransaksiFields();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data karyawan. ID tidak ditemukan.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Kesalahan saat memperbarui Data Karyawan: " + e.getMessage(), 
                                      "Kesalahan", JOptionPane.ERROR_MESSAGE);
    } 

}

    private void hapusKaryawan() {
    try {
        int id = Integer.parseInt(id_karyawan.getText()); 
        String sql = "DELETE FROM tb_karyawan WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        JOptionPane.showMessageDialog(this, "Data Karyawan berhasil dihapus.");
        loadDataKaryawan();
        clearKaryawanFields();
        loadComboBoxKaryawan();
        clearTransaksiFields();
    } catch (SQLException e) {
        System.out.println("Error Hapus Data Karyawan: " + e.getMessage());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "ID tidak valid");
    }
    loadComboBoxKaryawan();
}
    
//    CRUD tab proyek
    
    private void tambahProyek() {
        
    try {
        String sql = "INSERT INTO tb_proyek (nama_proyek, durasi_pengerjaan) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, tf_namaproyek.getText());  
        ps.setString(2, tf_durasiproyek.getText()); 
        ps.executeUpdate();
        JOptionPane.showMessageDialog(this, "Data Proyek berhasil ditambahkan.");
        loadDataProyek();
        clearProyekFields(); 
        loadComboBoxProyek();
        clearTransaksiFields();
    } catch (SQLException e) {
        System.out.println("Error Tambah Data Proyek: " + e.getMessage());
    }
}
    private void updateProyek() {
         if (id_proyek.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "ID proyek tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return; 
    }
    try {
        int id = Integer.parseInt(id_proyek.getText()); 
        String sql = "UPDATE tb_proyek SET nama_proyek = ?, durasi_pengerjaan = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, tf_namaproyek.getText()); 
        ps.setString(2, tf_durasiproyek.getText());  
        ps.setInt(3, id);
        
        
        ps.executeUpdate();
        JOptionPane.showMessageDialog(this, "Data Proyek berhasil diupdate.");
        loadDataProyek();
        clearProyekFields();  
        loadComboBoxProyek();
        clearTransaksiFields();
    } catch (SQLException e) {
        System.out.println("Error Update Data Proyek: " + e.getMessage());
    }
}
    private void hapusProyek() {
    try {
        int id = Integer.parseInt(id_proyek.getText()); 

        DefaultTableModel model = (DefaultTableModel) tbl_proyek.getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            int tableId = (int) model.getValueAt(i, 0); 
            if (tableId == id) {
                String sqlDelete = "DELETE FROM tb_proyek WHERE id = ?";
                PreparedStatement psDelete = conn.prepareStatement(sqlDelete);
                psDelete.setInt(1, id);
                psDelete.executeUpdate();
                
                // Hapus baris dari tabel GUI
                model.removeRow(i); 
                JOptionPane.showMessageDialog(this, "Data Proyek berhasil dihapus dari tabel dan database.");
                loadDataProyek();
                clearProyekFields();
                loadComboBoxProyek();
                clearTransaksiFields();
 
                clearProyekFields();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Data Proyek tidak ditemukan di tabel.");
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "ID tidak valid");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error menghapus data: " + e.getMessage());
    }
}
    
//    read combobox tab transaksi
    
    private void loadComboBoxKaryawan() {
    combo_karyawan.removeAllItems();
    try {
        String sql = "SELECT id, nama, jabatan, departemen FROM tb_karyawan";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String tampilan = String.format("ID: %d - %s (%s, %s)", 
                rs.getInt("id"),
                rs.getString("nama"),
                rs.getString("jabatan"),
                rs.getString("departemen")
            );
            combo_karyawan.addItem(tampilan);
        }
    } catch (SQLException e) {
        System.out.println("Kesalahan Memuat Data Karyawan: " + e.getMessage());
    }
}

    private void loadComboBoxProyek() {
    combo_proyek.removeAllItems();
    try {
        String sql = "SELECT id, nama_proyek, durasi_pengerjaan FROM tb_proyek";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String tampilan = String.format("ID: %d - %s (%s)", 
                rs.getInt("id"),
                rs.getString("nama_proyek"),
                rs.getString("durasi_pengerjaan")
            );
            combo_proyek.addItem(tampilan);
        }
    } catch (SQLException e) {
        System.out.println("Kesalahan Memuat Data Proyek: " + e.getMessage());
    }
}
    
    private int getSelectedId(String textComboBox) {
        if (textComboBox == null) return -1;
        try {
            String idPart = textComboBox.substring(textComboBox.indexOf("ID: ") + 4);
            return Integer.parseInt(idPart.substring(0, idPart.indexOf(" -")));
        } catch (Exception e) {
            System.out.println("Error parsing ID: " + e.getMessage());
            return -1;
        }
}
//    CRUD tab transaksi
        
    private void tambahTransaksi() {
        try {
            if (combo_karyawan.getSelectedItem() == null || combo_proyek.getSelectedItem() == null || tf_perantransaksi.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih karyawan, proyek, dan isi peran terlebih dahulu");
                return;
            }

            String sql = "INSERT INTO tb_transaksi (id_karyawan, id_proyek, durasi) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            int idKaryawan = getSelectedId(combo_karyawan.getSelectedItem().toString());
            int idProyek = getSelectedId(combo_proyek.getSelectedItem().toString());

            ps.setInt(1, idKaryawan);
            ps.setInt(2, idProyek);
            ps.setInt(3, Integer.parseInt(tf_perantransaksi.getText().trim()));

            int result = ps.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Data Transaksi berhasil ditambahkan");
                loadDataTransaksi();
                clearTransaksiFields();
            }
        } catch (SQLException e) {
            System.out.println("Kesalahan Menambah Data Transaksi: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal menambah data: " + e.getMessage());
        }
    }

    private void updateTransaksi() {
            if (tf_perantransaksi.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Peran tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return; 
    }
           PreparedStatement ps = null; 
    try {
        String sql = "UPDATE tb_transaksi SET durasi = ? WHERE id_karyawan = ? AND id_proyek = ?";
        ps = conn.prepareStatement(sql);

        int idKaryawan = getSelectedId(combo_karyawan.getSelectedItem().toString());
        int idProyek = getSelectedId(combo_proyek.getSelectedItem().toString());

        ps.setInt(1, Integer.parseInt(tf_perantransaksi.getText().trim()));
        ps.setInt(2, idKaryawan);
        ps.setInt(3, idProyek);

        int affectedRows = ps.executeUpdate(); 
        if (affectedRows > 0) {
            JOptionPane.showMessageDialog(this, "Data Transaksi berhasil diperbarui");
            loadDataTransaksi();
            clearTransaksiFields();
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada data transaksi yang diperbarui. Pastikan ID karyawan dan proyek benar.", 
                                          "Informasi", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Kesalahan Memperbarui Data Transaksi: " + e.getMessage(), 
                                      "Kesalahan", JOptionPane.ERROR_MESSAGE);
    } 

}

    private void hapusTransaksi() {
    // Memastikan ComboBox tidak kosong
    if (combo_karyawan.getSelectedItem() == null || combo_proyek.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Silakan pilih karyawan dan proyek sebelum menghapus transaksi.", 
                                      "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    PreparedStatement ps = null; 
    try {
        String sql = "DELETE FROM tb_transaksi WHERE id_karyawan = ? AND id_proyek = ?";
        ps = conn.prepareStatement(sql);
        
        int idKaryawan = getSelectedId(combo_karyawan.getSelectedItem().toString());
        int idProyek = getSelectedId(combo_proyek.getSelectedItem().toString());
        
        ps.setInt(1, idKaryawan);
        ps.setInt(2, idProyek);
        
        int affectedRows = ps.executeUpdate(); 
        if (affectedRows > 0) {
            JOptionPane.showMessageDialog(this, "Data Transaksi berhasil dihapus.");
            loadDataTransaksi();
            clearTransaksiFields();
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada transaksi yang dihapus. Pastikan ID karyawan dan proyek benar.", 
                                          "Informasi", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Kesalahan Menghapus Data Transaksi: " + e.getMessage(), 
                                      "Error", JOptionPane.ERROR_MESSAGE);
    } 

}
    
    private void clearKaryawanFields() {
        id_karyawan.setText("");
        tf_namakaryawan.setText("");
        tf_jabatankaryawan.setText("");
        tf_deparkaryawan.setText("");
    }

    private void clearProyekFields() {
        id_proyek.setText("");
        tf_namaproyek.setText("");
        tf_durasiproyek.setText("");
    }
    
    private void clearTransaksiFields() {
        combo_karyawan.setSelectedIndex(-1);
        combo_proyek.setSelectedIndex(-1);
        tf_perantransaksi.setText("");
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        JTabbedPane = new javax.swing.JTabbedPane();
        karyawan = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tf_deparkaryawan = new javax.swing.JTextField();
        tf_jabatankaryawan = new javax.swing.JTextField();
        tf_namakaryawan = new javax.swing.JTextField();
        id_karyawan = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_karyawan = new javax.swing.JTable();
        btn_Ckaryawan = new javax.swing.JButton();
        btn_Ukaryawan = new javax.swing.JButton();
        btn_Dkaryawan = new javax.swing.JButton();
        proyek = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tf_durasiproyek = new javax.swing.JTextField();
        tf_namaproyek = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        id_proyek = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_proyek = new javax.swing.JTable();
        btn_Cproyek = new javax.swing.JButton();
        btn_Uproyek = new javax.swing.JButton();
        btn_Dproyek = new javax.swing.JButton();
        transaksi = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        combo_karyawan = new javax.swing.JComboBox<>();
        combo_proyek = new javax.swing.JComboBox<>();
        tf_perantransaksi = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_transaksi = new javax.swing.JTable();
        btn_Ctransaksi = new javax.swing.JButton();
        btn_Utransaksi = new javax.swing.JButton();
        btn_Dtransaksi = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JTabbedPane.setBackground(new java.awt.Color(204, 204, 0));
        JTabbedPane.setForeground(new java.awt.Color(0, 0, 0));

        karyawan.setBackground(new java.awt.Color(255, 255, 255));
        karyawan.setForeground(new java.awt.Color(0, 0, 0));
        karyawan.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(204, 102, 0));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("TABEL KARYAWAN");
        jPanel3.add(jLabel2);

        karyawan.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(204, 102, 0));

        jPanel2.setBackground(new java.awt.Color(204, 204, 0));

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("ID :");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("NAMA :");

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("JABATAN :");

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("DEPARTEMEN :");

        tbl_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tbl_karyawan);

        btn_Ckaryawan.setText("SIMPAN");
        btn_Ckaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CkaryawanActionPerformed(evt);
            }
        });

        btn_Ukaryawan.setText("EDIT");
        btn_Ukaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_UkaryawanActionPerformed(evt);
            }
        });

        btn_Dkaryawan.setText("DELETE");
        btn_Dkaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DkaryawanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(64, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(id_karyawan)
                            .addComponent(tf_namakaryawan)
                            .addComponent(tf_jabatankaryawan)
                            .addComponent(tf_deparkaryawan))))
                .addGap(82, 82, 82))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(btn_Ckaryawan)
                .addGap(90, 90, 90)
                .addComponent(btn_Ukaryawan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Dkaryawan)
                .addGap(102, 102, 102))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(id_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tf_namakaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tf_jabatankaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tf_deparkaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Ckaryawan)
                    .addComponent(btn_Ukaryawan)
                    .addComponent(btn_Dkaryawan))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 610, 640));

        karyawan.add(jPanel4, java.awt.BorderLayout.CENTER);

        JTabbedPane.addTab("KARYAWAN", karyawan);

        proyek.setBackground(new java.awt.Color(204, 102, 0));
        proyek.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(204, 102, 0));

        jLabel6.setBackground(new java.awt.Color(204, 102, 0));
        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("TABEL PROYEK");
        jPanel5.add(jLabel6);

        proyek.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel6.setBackground(new java.awt.Color(204, 102, 0));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel7.setBackground(new java.awt.Color(204, 204, 0));

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("ID :");

        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("NAMA PROYEK :");

        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("DURASI PENGERJAAN : ");

        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("MINGGU");

        tbl_proyek.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tbl_proyek);

        btn_Cproyek.setText("SIMPAN");
        btn_Cproyek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CproyekActionPerformed(evt);
            }
        });

        btn_Uproyek.setText("EDIT");
        btn_Uproyek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_UproyekActionPerformed(evt);
            }
        });

        btn_Dproyek.setText("DELETE");
        btn_Dproyek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DproyekActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(tf_durasiproyek, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tf_namaproyek)
                    .addComponent(id_proyek))
                .addContainerGap(65, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(btn_Cproyek)
                        .addGap(96, 96, 96)
                        .addComponent(btn_Uproyek)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_Dproyek))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(74, 74, 74))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(id_proyek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(tf_namaproyek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(tf_durasiproyek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Cproyek)
                    .addComponent(btn_Uproyek)
                    .addComponent(btn_Dproyek))
                .addGap(23, 23, 23))
        );

        jPanel6.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 590, 620));

        proyek.add(jPanel6, java.awt.BorderLayout.CENTER);

        JTabbedPane.addTab("PROYEK", proyek);

        transaksi.setLayout(new java.awt.BorderLayout());

        jPanel8.setBackground(new java.awt.Color(204, 102, 0));

        jLabel11.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("TABEL TRANSAKSI");
        jPanel8.add(jLabel11);

        transaksi.add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jPanel9.setBackground(new java.awt.Color(204, 102, 0));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel10.setBackground(new java.awt.Color(204, 204, 0));

        jLabel12.setBackground(new java.awt.Color(0, 0, 0));
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("KARYAWAN :");

        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("PROYEK :");

        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("DURASI");

        tbl_transaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tbl_transaksi);

        btn_Ctransaksi.setText("SIMPAN");
        btn_Ctransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CtransaksiActionPerformed(evt);
            }
        });

        btn_Utransaksi.setText("EDIT");
        btn_Utransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_UtransaksiActionPerformed(evt);
            }
        });

        btn_Dtransaksi.setText("DELETE");
        btn_Dtransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DtransaksiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addGap(31, 31, 31)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(combo_karyawan, 0, 301, Short.MAX_VALUE)
                    .addComponent(combo_proyek, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tf_perantransaksi))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(btn_Ctransaksi)
                        .addGap(101, 101, 101)
                        .addComponent(btn_Utransaksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_Dtransaksi))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap(80, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(69, 69, 69))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(combo_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(combo_proyek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(tf_perantransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Ctransaksi)
                    .addComponent(btn_Utransaksi)
                    .addComponent(btn_Dtransaksi))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 590, 620));

        transaksi.add(jPanel9, java.awt.BorderLayout.CENTER);

        JTabbedPane.addTab("TRANSAKSI", transaksi);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(JTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(JTabbedPane)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_CkaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CkaryawanActionPerformed
        // TODO add your handling code here:
        tambahKaryawan();
    }//GEN-LAST:event_btn_CkaryawanActionPerformed

    private void btn_UkaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_UkaryawanActionPerformed
        // TODO add your handling code here:
        updateKaryawan();
    }//GEN-LAST:event_btn_UkaryawanActionPerformed

    private void btn_DkaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DkaryawanActionPerformed
        // TODO add your handling code here:
        hapusKaryawan();
    }//GEN-LAST:event_btn_DkaryawanActionPerformed

    private void btn_CproyekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CproyekActionPerformed
        // TODO add your handling code here:
        tambahProyek();
    }//GEN-LAST:event_btn_CproyekActionPerformed

    private void btn_UproyekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_UproyekActionPerformed
        // TODO add your handling code here:
        updateProyek();
    }//GEN-LAST:event_btn_UproyekActionPerformed

    private void btn_DproyekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DproyekActionPerformed
        // TODO add your handling code here:
        hapusProyek();
    }//GEN-LAST:event_btn_DproyekActionPerformed

    private void btn_CtransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CtransaksiActionPerformed
        // TODO add your handling code here:
        tambahTransaksi();
    }//GEN-LAST:event_btn_CtransaksiActionPerformed

    private void btn_UtransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_UtransaksiActionPerformed
        // TODO add your handling code here:
        updateTransaksi();
    }//GEN-LAST:event_btn_UtransaksiActionPerformed

    private void btn_DtransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DtransaksiActionPerformed
        // TODO add your handling code here:
        hapusTransaksi();
    }//GEN-LAST:event_btn_DtransaksiActionPerformed

    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(manajemenkaryawandanproyek.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(manajemenkaryawandanproyek.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(manajemenkaryawandanproyek.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(manajemenkaryawandanproyek.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new manajemenkaryawandanproyek().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane JTabbedPane;
    private javax.swing.JButton btn_Ckaryawan;
    private javax.swing.JButton btn_Cproyek;
    private javax.swing.JButton btn_Ctransaksi;
    private javax.swing.JButton btn_Dkaryawan;
    private javax.swing.JButton btn_Dproyek;
    private javax.swing.JButton btn_Dtransaksi;
    private javax.swing.JButton btn_Ukaryawan;
    private javax.swing.JButton btn_Uproyek;
    private javax.swing.JButton btn_Utransaksi;
    private javax.swing.JComboBox<String> combo_karyawan;
    private javax.swing.JComboBox<String> combo_proyek;
    private javax.swing.JTextField id_karyawan;
    private javax.swing.JTextField id_proyek;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JPanel karyawan;
    private javax.swing.JPanel proyek;
    private javax.swing.JTable tbl_karyawan;
    private javax.swing.JTable tbl_proyek;
    private javax.swing.JTable tbl_transaksi;
    private javax.swing.JTextField tf_deparkaryawan;
    private javax.swing.JTextField tf_durasiproyek;
    private javax.swing.JTextField tf_jabatankaryawan;
    private javax.swing.JTextField tf_namakaryawan;
    private javax.swing.JTextField tf_namaproyek;
    private javax.swing.JTextField tf_perantransaksi;
    private javax.swing.JPanel transaksi;
    // End of variables declaration//GEN-END:variables
}
