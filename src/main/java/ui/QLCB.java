/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package ui;

import java.awt.Color;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import model.Excel;
import model.SinhVien;
import utils.JDBCUtils;

/**
 *
 * @author duyplus
 */
public class QLCB extends javax.swing.JFrame {

    ArrayList<SinhVien> list = new ArrayList<>();
    DefaultTableModel model;
    // Biến dùng chung
    int index = 0;
    String imgName = "";
    public static Connection conn = null; // Kết nối với sql
    public static PreparedStatement ps = null; // Câu lệnh SQL được biên dịch trước
    public static Statement st = null; // Thi hành câu lệnh tùy ý tại thời điểm chạy
    public static ResultSet rs = null; // Trả về kết quả truy vấn

    /**
     * Creates new form QLSV
     */
    public QLCB() {
        initComponents();
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(231, 228, 190));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        conn = JDBCUtils.getConnection();

        model = (DefaultTableModel) tblQLSV.getModel();
        model.setRowCount(0);
        list = getStudent();
        loadDbTable();
        showDetail(0);

        tblQLSV.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblQLSV.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblQLSV.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblQLSV.getColumnModel().getColumn(3).setPreferredWidth(30);
        tblQLSV.getColumnModel().getColumn(4).setPreferredWidth(10);
        tblQLSV.getColumnModel().getColumn(5).setPreferredWidth(30);
        tblQLSV.getColumnModel().getColumn(6).setPreferredWidth(10);
        tblQLSV.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    public void ChooseImage() {
        JFileChooser fc = new JFileChooser("src\\image\\");
        FileFilter filter = new FileNameExtensionFilter("Image Files", "gif", "jpeg", "jpg", "png");
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        int kq = fc.showOpenDialog(fc);
        if (kq == JFileChooser.APPROVE_OPTION) {
            imgName = fc.getSelectedFile().getName();
            upImage(imgName);
        } else {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn ảnh...");
        }
    }

    public void upImage(String imgName) {
        ImageIcon ii = new ImageIcon("src\\image\\" + imgName);
        Image img = ii.getImage();
        ImageIcon icon = new ImageIcon(img.getScaledInstance(lblImage.getWidth(), lblImage.getHeight(), Image.SCALE_SMOOTH));
        lblImage.setIcon(icon);
    }

    public ArrayList<SinhVien> getStudent() {
        try {
            model.setRowCount(0);
            conn = JDBCUtils.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM STUDENTS");
            while (rs.next()) {
                SinhVien sv = new SinhVien();
                sv.setMaSV(rs.getString(1));
                sv.setHoTen(rs.getString(2));
                sv.setEmail(rs.getString(3));
                sv.setSoDT(rs.getString(4));
                sv.setGioiTinh(rs.getBoolean(5));
                sv.setDiaChi(rs.getString(6));
                sv.setHinh(rs.getString(7));
                list.add(sv);
            }
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
        return list;
    }

    public void loadDbTable() {
        try {
            model.setRowCount(0);
            conn = JDBCUtils.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM STUDENTS");
            if (rs.isBeforeFirst() == false) {
                JOptionPane.showMessageDialog(this, "Chưa có sinh viên nào!");
                return;
            }
            while (rs.next()) {
                Vector row = new Vector();
                row.add(rs.getString(1));
                row.add(rs.getString(2));
                row.add(rs.getString(3));
                row.add(rs.getString(4));
                String gt;
                if (rs.getBoolean(5) == true) {
                    gt = "Nam";
                } else {
                    gt = "Nữ";
                }
                row.add(gt);
                row.add(rs.getString(6));
                row.add(rs.getString(7));
                model.addRow(row);
            }
            tblQLSV.setModel(model);
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showDetail(int row) {
        row = tblQLSV.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtMaSV.setText(tblQLSV.getValueAt(row, 0).toString());
        txtHoTen.setText(tblQLSV.getValueAt(row, 1).toString());
        txtEmail.setText(tblQLSV.getValueAt(row, 2).toString());
        txtSoDT.setText(tblQLSV.getValueAt(row, 3).toString());
        Boolean gt = Boolean.parseBoolean(tblQLSV.getValueAt(row, 4).toString());
        if (gt) {
            rdoNam.setSelected(true);
        } else {
            rdoNu.setSelected(true);
        }
        txtDiaChi.setText(tblQLSV.getValueAt(row, 5).toString());
        upImage(tblQLSV.getValueAt(row, 6).toString());
    }

    public void searchSV() {
        try {
            model.setRowCount(0);
            conn = JDBCUtils.getConnection();
            String sql = "SELECT * FROM STUDENTS WHERE MASV = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, txtSearch.getText());
            rs = ps.executeQuery();
            boolean isOk = false;
            while (rs.next()) {
                JOptionPane.showMessageDialog(this, "Đã tìm thấy sinh viên!");
                txtMaSV.setText(rs.getString(1));
                txtHoTen.setText(rs.getString(2));
                txtEmail.setText(rs.getString(3));
                txtSoDT.setText(rs.getString(4));
                if (isOk) {
                    rdoNam.setSelected(true);
                } else {
                    rdoNu.setSelected(true);
                }
                txtDiaChi.setText(rs.getString(6));
                lblImage.setText(rs.getString(7));
                isOk = true;
            }
            if (!isOk) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên!");
                txtMaSV.requestFocus();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void addStudent() {
        txtMaSV.setText("");
        txtHoTen.setText("");
        txtEmail.setText("");
        txtSoDT.setText("");
        txtDiaChi.setText("");
        bgGT.clearSelection();
        tblQLSV.clearSelection();
        lblImage.setIcon(null);
    }

    public void deleteStudent() {
        try {
            conn = JDBCUtils.getConnection();
            int chon = JOptionPane.showConfirmDialog(this, "Bạn có muốn xoá sinh viên này không?",
                    "THÔNG BÁO", JOptionPane.YES_NO_OPTION);
            if (index == -1) {
                JOptionPane.showMessageDialog(this, "Bạn chưa chọn sinh viên cần xóa!");
            } else {
                if (chon == JOptionPane.YES_OPTION) {
                    String sql = "DELETE FROM STUDENTS WHERE MASV = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, txtMaSV.getText());
                    ps.executeUpdate();
                    list.remove(index);
                    JOptionPane.showMessageDialog(this, "Xóa sinh viên thành công!");
                }
            }
            conn.close();
            loadDbTable();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Thất bại!");
        }
    }

    public void updateStudent() {
        try {
            conn = JDBCUtils.getConnection();
            index = tblQLSV.getSelectedRow();
            if (index == -1) {
                JOptionPane.showMessageDialog(this, "Bạn chưa chọn sinh viên cần cập nhật!");
            } else {
                String query = "UPDATE STUDENTS SET HOTEN = ?, EMAIL = ?, SODT = ?, GIOITINH = ?,"
                        + " DIACHI = ?, HINH = ? WHERE MASV = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, txtHoTen.getText());
                ps.setString(2, txtEmail.getText());
                ps.setString(3, txtSoDT.getText());
                boolean gt;
                if (rdoNam.isSelected()) {
                    gt = true;
                } else {
                    gt = false;
                }
                ps.setBoolean(4, gt);
                ps.setString(5, txtDiaChi.getText());
                ps.setString(6, imgName);
                ps.setString(7, txtMaSV.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thành công!");
            }
            conn.close();
            loadDbTable();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Thất bại!");
        }
    }

    public void saveStudent() {
        try {
            conn = JDBCUtils.getConnection();
            SinhVien sv = new SinhVien();
            if (Valdidate()) {
                sv.setMaSV(txtMaSV.getText());
                sv.setHoTen(txtHoTen.getText());
                sv.setEmail(txtEmail.getText());
                sv.setSoDT(txtSoDT.getText());
                boolean gt;
                if (rdoNam.isSelected()) {
                    gt = true;
                } else {
                    gt = false;
                }
                sv.setGioiTinh(gt);
                sv.setDiaChi(txtDiaChi.getText());
                sv.setHinh(imgName);
            }
            String sql = "INSERT INTO STUDENTS VALUES(?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, sv.getMaSV());
            ps.setString(2, sv.getHoTen());
            ps.setString(3, sv.getEmail());
            ps.setString(4, sv.getSoDT());
            ps.setBoolean(5, sv.isGioiTinh());
            ps.setString(6, sv.getDiaChi());
            ps.setString(7, imgName);
            int row = ps.executeUpdate();
            if (row != 0) {
                JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
                list.add(sv);
            }
            conn.close();
            loadDbTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi! Trùng Mã SV không thể lưu sinh viên mới.");
        }
    }

    public boolean Valdidate() {
        if (txtMaSV.getText().equals("") || txtHoTen.getText().equals("") || txtEmail.getText().equals("")
                || txtSoDT.getText().equals("") || txtDiaChi.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Hãy nhập đủ dữ liệu sau đó ấn Save!");
            return false;
        } else if (!(txtEmail.getText()).matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
            JOptionPane.showMessageDialog(this, "Nhập sai định dạng Email!");
            txtEmail.requestFocus();
            return false;
        } else if (!rdoNam.isSelected() && !rdoNu.isSelected()) {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn giới tính");
            return false;
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgGT = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblQLSV = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblTitle2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();
        lblMaSV = new javax.swing.JLabel();
        txtMaSV = new javax.swing.JTextField();
        lblHoTen = new javax.swing.JLabel();
        txtHoTen = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblSDT = new javax.swing.JLabel();
        txtSoDT = new javax.swing.JTextField();
        lblGioiTinh = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        rdoNam = new javax.swing.JRadioButton();
        rdoNu = new javax.swing.JRadioButton();
        lblDiaChi = new javax.swing.JLabel();
        txtDiaChi = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblQLSV.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblQLSV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ SV", "HỌ TÊN", "EMAIL", "SỐ ĐT", "GIỚI TÍNH", "ĐỊA CHỈ", "HÌNH"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblQLSV.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tblQLSV.setFillsViewportHeight(true);
        tblQLSV.setIntercellSpacing(new java.awt.Dimension(5, 5));
        tblQLSV.setShowHorizontalLines(true);
        tblQLSV.setShowVerticalLines(true);
        tblQLSV.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblQLSVMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblQLSV);

        jPanel3.setBackground(new java.awt.Color(0, 204, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lblTitle2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTitle2.setForeground(new java.awt.Color(0, 0, 204));
        lblTitle2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle2.setText("QUẢN LÝ THÔNG TIN SINH VIÊN");
        lblTitle2.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTitle2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(30, 139, 195));

        lblSearch.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(255, 255, 255));
        lblSearch.setText("TÌM KIẾM");

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/search.png"))); // NOI18N
        btnSearch.setContentAreaFilled(false);
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(258, 258, 258)
                .addComponent(lblSearch)
                .addGap(44, 44, 44)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(txtSearch)
            .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        btnNew.setBackground(new java.awt.Color(0, 153, 255));
        btnNew.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnNew.setForeground(new java.awt.Color(255, 255, 255));
        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/add.png"))); // NOI18N
        btnNew.setText("LÀM MỚI");
        btnNew.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNew.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnNew.setIconTextGap(10);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnSave.setBackground(new java.awt.Color(0, 153, 255));
        btnSave.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/save.png"))); // NOI18N
        btnSave.setText("THÊM SINH VIÊN");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnSave.setIconTextGap(10);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(0, 153, 255));
        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/delete.png"))); // NOI18N
        btnDelete.setText("XOÁ SINH VIÊN");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnDelete.setIconTextGap(10);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(0, 153, 255));
        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/update.png"))); // NOI18N
        btnUpdate.setText("CẬP NHẬT SINH VIÊN");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnUpdate.setIconTextGap(10);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnExport.setBackground(new java.awt.Color(0, 153, 255));
        btnExport.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnExport.setForeground(new java.awt.Color(255, 255, 255));
        btnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/export.png"))); // NOI18N
        btnExport.setText("XUẤT EXCEL");
        btnExport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExport.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnExport.setIconTextGap(10);
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(275, 275, 275)
                        .addComponent(btnExport)))
                .addContainerGap(245, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        lblImage.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/open.png"))); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });

        lblMaSV.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblMaSV.setText("MÃ SINH VIÊN");

        txtMaSV.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMaSV.setMargin(new java.awt.Insets(2, 4, 2, 4));

        lblHoTen.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblHoTen.setText("HỌ TÊN");

        txtHoTen.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtHoTen.setMargin(new java.awt.Insets(2, 4, 2, 4));

        lblEmail.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblEmail.setText("EMAIL");

        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtEmail.setMargin(new java.awt.Insets(2, 4, 2, 4));

        lblSDT.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblSDT.setText("SỐ ĐIỆN THOẠI");

        txtSoDT.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSoDT.setMargin(new java.awt.Insets(2, 4, 2, 4));

        lblGioiTinh.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblGioiTinh.setText("GIỚI TÍNH");

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 0));

        rdoNam.setBackground(new java.awt.Color(255, 255, 255));
        rdoNam.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rdoNam.setText("Nam");

        rdoNu.setBackground(new java.awt.Color(255, 255, 255));
        rdoNu.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rdoNu.setText("Nữ");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(rdoNam)
                .addGap(18, 18, 18)
                .addComponent(rdoNu)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoNam)
                    .addComponent(rdoNu)))
        );

        lblDiaChi.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblDiaChi.setText("ĐỊA CHỈ");

        txtDiaChi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDiaChi.setMargin(new java.awt.Insets(2, 4, 2, 4));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtSoDT, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtHoTen, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtMaSV, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblMaSV, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblHoTen, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblEmail, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblSDT, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblGioiTinh, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblDiaChi, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblImage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMaSV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaSV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblHoTen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSDT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSoDT, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblGioiTinh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDiaChi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblQLSVMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblQLSVMouseClicked
        // TODO add your handling code here:
        int row = tblQLSV.getSelectedRow();
        showDetail(row);
    }//GEN-LAST:event_tblQLSVMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        searchSV();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        addStudent();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveStudent();
        addStudent();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteStudent();
        addStudent();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        updateStudent();
        addStudent();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
        Excel ex = new Excel();
        ex.exStudent();
    }//GEN-LAST:event_btnExportActionPerformed

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked
        // TODO add your handling code here:
        ChooseImage();
    }//GEN-LAST:event_lblImageMouseClicked

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(QLCB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QLCB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QLCB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QLCB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
//                boolean ok = true;
//                int msg = JOptionPane.showConfirmDialog(null, "Bạn cần phải đăng nhập để truy cập trang quản lý!"
//                        + "\n(CHỈ DÀNH CHO PHÒNG ĐÀO TẠO)"
//                        + "\n\n                (Chọn 'YES' để tiếp tục)", "THÔNG BÁO", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//                if (msg == JOptionPane.YES_OPTION) {
//                    if (ok) {
//                        new LOGIN().setVisible(true);
//                    } else {
                        new QLCB().setVisible(true);
//                    }
//                } else {
//                    return;
//                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgGT;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDiaChi;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGioiTinh;
    private javax.swing.JLabel lblHoTen;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblMaSV;
    private javax.swing.JLabel lblSDT;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblTitle2;
    private javax.swing.JRadioButton rdoNam;
    private javax.swing.JRadioButton rdoNu;
    private javax.swing.JTable tblQLSV;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtMaSV;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSoDT;
    // End of variables declaration//GEN-END:variables
}
