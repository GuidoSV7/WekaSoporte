package com.mycompany.wekasoporte;

import javax.swing.*;
import java.awt.*;

public class WekaSoporte extends JFrame {
    private JPanel mainPanel;
    private JComboBox<String>[] inputFields;
    private JLabel[] inputLabels;
    private JButton predictButton;
    private JButton clearButton;
    private JLabel resultLabel;

    private final String[][] OPTIONS = {
        // checking_status
        {"", "<0", "0<=X<200", ">=200", "no checking"},
        // foreign_worker
        {"", "no", "yes"},
        // duration - este será un JTextField
        null,
        // existing_credits - este será un JTextField
        null,
        // property_magnitude
        {"", "no known property", "car", "life insurance", "real estate"},
        // own_telephone
        {"", "none", "yes"},
        // job
        {"", "unemployed/unskilled non res", "unskilled resident", "skilled", "high qualif/self emp/mgmt"},
        // purpose
        {"", "new car", "used car", "furniture/equipment", "radio/tv", "domestic appliance", 
         "repairs", "education", "vacation", "retraining", "business", "other"},
        // other_parties
        {"", "none", "co applicant", "guarantor"},
        // savings_status
        {"", "<100", "100<=X<500", "500<=X<1000", ">=1000", "no known savings"},
        // credit_history
        {"", "no credits/all paid", "all paid", "existing paid", "delayed previously", "critical/other existing credit"},
        // credit_amount - este será un JTextField
        null,
        // personal_status
        {"", "male div/sep", "female div/dep/mar", "male single", "male mar/wid", "female single"},
        // residence_since - este será un JTextField
        null,
        // housing
        {"", "rent", "own", "for free"},
        // age - este será un JTextField
        null
    };

    private final String[] ATRIBUTOS = {
        "Estado de cuenta",
        "Trabajador extranjero",
        "Duración en meses",
        "Créditos existentes",
        "Tipo de propiedad",
        "Teléfono",
        "Trabajo",
        "Propósito",
        "Otros deudores",
        "Estado de ahorros",
        "Historial crediticio",
        "Monto del crédito",
        "Estado civil",
        "Tiempo en residencia",
        "Vivienda",
        "Edad"
    };

    @SuppressWarnings("unchecked")
    public WekaSoporte() {
        setTitle("Evaluación de Crédito");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de título
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Sistema de Evaluación de Crédito");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        contentPanel.add(titlePanel, BorderLayout.NORTH);

        // Panel de campos
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        inputFields = new JComboBox[ATRIBUTOS.length];
        inputLabels = new JLabel[ATRIBUTOS.length];

        for (int i = 0; i < ATRIBUTOS.length; i++) {
            // Etiqueta
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.4;
            inputLabels[i] = new JLabel(ATRIBUTOS[i] + ":");
            mainPanel.add(inputLabels[i], gbc);

            // Campo de entrada
            gbc.gridx = 1;
            gbc.weightx = 0.6;
            
            if (OPTIONS[i] != null) {
                // Crear ComboBox para opciones predefinidas
                inputFields[i] = new JComboBox<>(OPTIONS[i]);
                inputFields[i].setPreferredSize(new Dimension(200, 25));
                // Renderizador personalizado para el item vacío
                inputFields[i].setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value.toString().isEmpty()) {
                            setText(" ");
                        }
                        return this;
                    }
                });
                mainPanel.add(inputFields[i], gbc);
            } else {
                // Crear TextField para entrada numérica
                JTextField textField = new JTextField(15);
                textField.setPreferredSize(new Dimension(200, 25));
                mainPanel.add(textField, gbc);
            }
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones y resultado
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        predictButton = new JButton("Evaluar");
        predictButton.setFont(new Font("Arial", Font.BOLD, 14));
        predictButton.setBackground(new Color(66, 134, 244));
        predictButton.setForeground(Color.WHITE);
        predictButton.setFocusPainted(false);
        predictButton.addActionListener(e -> evaluar());

        clearButton = new JButton("Limpiar");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.addActionListener(e -> limpiarCampos());

        resultLabel = new JLabel("Resultado: Pendiente de evaluación");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));

        bottomPanel.add(predictButton);
        bottomPanel.add(clearButton);
        bottomPanel.add(resultLabel);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(contentPanel);
    }

    private void limpiarCampos() {
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setSelectedIndex(0);
            } else if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            }
        }
        resultLabel.setText("Resultado: Pendiente de evaluación");
    }

    private void evaluar() {
        // Aquí irá la lógica de evaluación cuando implementemos el árbol de decisión
        try {
            // Recoger valores
            String checking = (String) inputFields[0].getSelectedItem();
            String foreignWorker = (String) inputFields[1].getSelectedItem();
            
            if (checking.isEmpty() || foreignWorker.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione al menos Estado de cuenta y Trabajador extranjero",
                    "Campos requeridos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Aquí continuará la implementación de la lógica del árbol de decisión
            resultLabel.setText("Resultado: Procesando evaluación...");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al procesar la evaluación: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WekaSoporte frame = new WekaSoporte();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}