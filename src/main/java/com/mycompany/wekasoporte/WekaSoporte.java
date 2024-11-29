package com.mycompany.wekasoporte;

import javax.swing.*;
import java.awt.*;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.bayes.BayesNet;
import weka.core.SerializationHelper;

public class WekaSoporte extends JFrame {
    private JPanel mainPanel;
    private JComboBox<String>[] inputFields;
    private JTextField[] numericFields;
    private JLabel[] inputLabels;
    private JButton predictButton;
    private JButton clearButton;
    private JLabel resultLabel;
    private BayesNet classifier;
    private Instances datasetHeader;

    private final String[][] OPTIONS = {
        // checking_status
        {"", "<0", "0<=X<200", ">=200", "no checking"},
        // duration - TextField
        null,
        // credit_history
        {"", "no credits/all paid", "all paid", "existing paid", "delayed previously", "critical/other existing credit"},
        // purpose
        {"", "new car", "used car", "furniture/equipment", "radio/tv", "domestic appliance", 
         "repairs", "education", "vacation", "retraining", "business", "other"},
        // credit_amount - TextField
        null,
        // savings_status
        {"", "<100", "100<=X<500", "500<=X<1000", ">=1000", "no known savings"},
        // employment
        {"", "unemployed", "< 1 year", "1<=X<4", "4<=X<7", ">= 7 years"},
        // installment_commitment - TextField
        null,
        // personal_status
        {"", "male div/sep", "female div/dep/mar", "male single", "male mar/wid", "female single"},
        // other_parties
        {"", "none", "co applicant", "guarantor"},
        // residence_since - TextField
        null,
        // property_magnitude
        {"", "no known property", "car", "life insurance", "real estate"},
        // age - TextField
        null,
        // other_payment_plans
        {"", "bank", "stores", "none"},
        // housing
        {"", "rent", "own", "for free"},
        // existing_credits - TextField
        null,
        // job
        {"", "unemployed/unskilled non res", "unskilled resident", "skilled", "high qualif/self emp/mgmt"},
        // num_dependents - TextField
        null,
        // own_telephone
        {"", "none", "yes"},
        // foreign_worker
        {"", "yes", "no"}
    };

    private final String[] ATRIBUTOS = {
        "Estado de cuenta",
        "Duración en meses",
        "Historial crediticio",
        "Propósito",
        "Monto del crédito",
        "Estado de ahorros",
        "Empleo",
        "Compromiso de cuotas",
        "Estado civil",
        "Otros deudores",
        "Tiempo en residencia",
        "Tipo de propiedad",
        "Edad",
        "Otros planes de pago",
        "Vivienda",
        "Créditos existentes",
        "Trabajo",
        "Número de dependientes",
        "Teléfono propio",
        "Trabajador extranjero"
    };

    @SuppressWarnings("unchecked")
    public WekaSoporte() {
        setTitle("Evaluación de Crédito - BayesNet");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);

        initializeComponents();
        try {
            cargarModelo();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar el modelo BayesNet: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Sistema de Evaluación de Crédito");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        contentPanel.add(titlePanel, BorderLayout.NORTH);


        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        inputFields = new JComboBox[ATRIBUTOS.length];
        numericFields = new JTextField[7]; // Para campos numéricos
        inputLabels = new JLabel[ATRIBUTOS.length];
        int numericFieldIndex = 0;

        for (int i = 0; i < ATRIBUTOS.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.4;
            inputLabels[i] = new JLabel(ATRIBUTOS[i] + ":");
            mainPanel.add(inputLabels[i], gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.6;
            
            if (OPTIONS[i] != null) {
                inputFields[i] = new JComboBox<>(OPTIONS[i]);
                inputFields[i].setPreferredSize(new Dimension(200, 25));
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
                JTextField textField = new JTextField(15);
                textField.setPreferredSize(new Dimension(200, 25));
                numericFields[numericFieldIndex++] = textField;
                mainPanel.add(textField, gbc);
            }
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
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

    private void cargarModelo() throws Exception {
        String modelPath = "model.model";
        String arffPath = "credit.g.arff";
        
        classifier = (BayesNet) SerializationHelper.read(modelPath);
        DataSource source = new DataSource(arffPath);
        datasetHeader = source.getDataSet();
        if (datasetHeader.classIndex() == -1) {
            datasetHeader.setClassIndex(datasetHeader.numAttributes() - 1);
        }
    }

    private void evaluar() {
        try {
            double[] valores = new double[datasetHeader.numAttributes()];

    
            for (int i = 0; i < inputFields.length; i++) {
                if (inputFields[i] != null) {
                    String selectedValue = (String) inputFields[i].getSelectedItem();
                    if (selectedValue.isEmpty()) {
                        throw new Exception("Por favor complete todos los campos");
                    }
                    valores[i] = inputFields[i].getSelectedIndex() - 1;
                }
            }

            // Procesar campos numéricos
            int numericFieldIndex = 0;
            for (int i = 0; i < OPTIONS.length; i++) {
                if (OPTIONS[i] == null) {
                    String value = numericFields[numericFieldIndex].getText();
                    if (value.isEmpty()) {
                        throw new Exception("Por favor complete todos los campos numéricos");
                    }
                    valores[i] = Double.parseDouble(value);
                    numericFieldIndex++;
                }
            }

            DenseInstance instancia = new DenseInstance(1.0, valores);
            instancia.setDataset(datasetHeader);

            double[] probabilidades = classifier.distributionForInstance(instancia);
            double probabilidadBuenCredito = probabilidades[0] * 100;
            String porcentaje = String.format("%.2f", probabilidadBuenCredito);

            if (probabilidadBuenCredito > 50.0) {
                resultLabel.setText("<html><div style='color:green; padding:10px;'>" +
                                  "✓ APROBADO<br>" +
                                  "Probabilidad: " + porcentaje + "%</div></html>");
            } else {
                resultLabel.setText("<html><div style='color:red; padding:10px;'>" +
                                  "✗ RECHAZADO<br>" +
                                  "Probabilidad: " + porcentaje + "%</div></html>");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Por favor, ingrese valores numéricos válidos",
                "Error de entrada",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error en la evaluación: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        for (JComboBox<String> comboBox : inputFields) {
            if (comboBox != null) {
                comboBox.setSelectedIndex(0);
            }
        }
        for (JTextField textField : numericFields) {
            if (textField != null) {
                textField.setText("");
            }
        }
        resultLabel.setText("Resultado: Pendiente de evaluación");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            WekaSoporte frame = new WekaSoporte();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}