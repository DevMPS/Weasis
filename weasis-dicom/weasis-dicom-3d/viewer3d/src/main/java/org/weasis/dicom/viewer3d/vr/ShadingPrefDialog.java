/*
 * Copyright (c) 2023 Weasis Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.DecFormatter;
import org.weasis.core.api.gui.util.GuiUtils;
import org.weasis.core.api.gui.util.JSliderW;
import org.weasis.core.api.gui.util.SliderChangeListener;
import org.weasis.core.api.util.FontItem;
import org.weasis.core.api.util.ResourceUtil;
import org.weasis.core.api.util.ResourceUtil.ActionIcon;
import org.weasis.core.util.StringUtil;

public class ShadingPrefDialog extends JDialog {

  private static final int MAX_SLIDER_VALUE = 512;
  //  private final float ambient;
  //  private final float diffuse;
  //  private final float specular;
  private final float specularPower;
  private final View3d view3d;
  //  JSliderW ambientSlider;
  //  JSliderW diffuseSlider;
  //  JSliderW specularSlider;
  JSliderW powerSlider;

  private boolean render = true;

  public ShadingPrefDialog(View3d view3d) {
    super(
        SwingUtilities.getWindowAncestor(view3d),
        "Shading Options",
        ModalityType.APPLICATION_MODAL);
    this.view3d = view3d;
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setIconImage(ResourceUtil.getIcon(ActionIcon.VOLUME).getImage());

    ShadingOptions options = view3d.getRenderingLayer().getShadingOptions();
    //    this.ambient = options.getAmbient();
    //    this.diffuse = options.getDiffuse();
    //    this.specular = options.getSpecular();
    this.specularPower = options.getSpecularPower();
    init();
    GuiUtils.setPreferredWidth(this, 550);
    pack();
  }

  private void init() {
    JPanel contentPane = GuiUtils.getVerticalBoxLayoutPanel();
    contentPane.setBorder(GuiUtils.getEmptyBorder(10, 15, 10, 15));

    // ambientSlider = createSlider("Ambient", 0, 1, realToSlider(ambient));
    // contentPane.add(GuiUtils.boxVerticalStrut(Insertable.BLOCK_SEPARATOR));
    // contentPane.add(ambientSlider);
    //
    // diffuseSlider = createSlider("Diffuse", 0, 1, realToSlider(diffuse));
    // contentPane.add(GuiUtils.boxVerticalStrut(Insertable.BLOCK_SEPARATOR));
    // contentPane.add(diffuseSlider);
    //
    // specularSlider = createSlider("Specular", 0, 1, realToSlider(specular));
    // contentPane.add(GuiUtils.boxVerticalStrut(Insertable.BLOCK_SEPARATOR));
    // contentPane.add(specularSlider);

    powerSlider = createSlider("Power", 1, 100, realToSliderPowerValue(specularPower));
    contentPane.add(GuiUtils.boxVerticalStrut(Insertable.BLOCK_SEPARATOR));
    contentPane.add(powerSlider);
    updateSliderText();

    JButton restoreButton = new JButton("Restore default values");

    restoreButton.addActionListener(
        e -> {
          Preset p = view3d.getVolumePreset();
          render = false;
          // ambientSlider.setValue(realToSlider(ambient));
          // diffuseSlider.setValue(realToSlider(diffuse));
          // specularSlider.setValue(realToSlider(specular));
          powerSlider.setValue(realToSliderPowerValue(specularPower));
          updateSliderText();
          render = true;
          updateValues(p.getSpecularPower());
        });
    contentPane.add(GuiUtils.getFlowLayoutPanel(FlowLayout.TRAILING, 5, 5, restoreButton));

    JButton okButton = new JButton("OK");
    okButton.addActionListener(e -> dispose());
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(
        e -> {
          updateValues(specularPower);
          dispose();
        });

    JPanel panel =
        GuiUtils.getFlowLayoutPanel(
            FlowLayout.TRAILING, 5, 5, okButton, GuiUtils.boxHorizontalStrut(20), cancelButton);
    panel.setBorder(GuiUtils.getEmptyBorder(20, 15, 10, 15));
    contentPane.add(panel);
    contentPane.add(GuiUtils.boxYLastElement(1));
    setContentPane(contentPane);
  }

  private JSliderW createSlider(String title, double min, double max, int value) {
    DefaultBoundedRangeModel model = new DefaultBoundedRangeModel(value, 0, 0, MAX_SLIDER_VALUE);
    TitledBorder titledBorder =
        new TitledBorder(
            BorderFactory.createEmptyBorder(),
            title + StringUtil.COLON_AND_SPACE + model.getValue(),
            TitledBorder.LEADING,
            TitledBorder.DEFAULT_POSITION,
            FontItem.MEDIUM.getFont(),
            null);
    JSliderW s = new JSliderW(model);
    s.setLabelDivision(2);
    s.setDisplayValueInTitle(true);
    s.setPaintTicks(true);
    s.setShowLabels(true);
    s.setBorder(titledBorder);
    if (s.isShowLabels()) {
      s.setPaintLabels(true);
      SliderChangeListener.setSliderLabelValues(
          s, model.getMinimum(), model.getMaximum(), min, max);
    }
    s.addChangeListener(
        l -> {
          ShadingOptions options = view3d.getRenderingLayer().getShadingOptions();
          String val = "";
          if (render) {
            if (s == powerSlider) {
              float v = sliderToRealPowerValue(model.getValue());
              options.setSpecularPower(v);
              val = DecFormatter.oneDecimal(v);
            }
            //            else if (s == ambientSlider) {
            //              float v = sliderToReal(model.getValue());
            //              options.setAmbient(v);
            //              val = DecFormatter.twoDecimal(v);
            //            } else if (s == diffuseSlider) {
            //              float v = sliderToReal(model.getValue());
            //              options.setDiffuse(v);
            //              val = DecFormatter.twoDecimal(v);
            //            } else if (s == specularSlider) {
            //              float v = sliderToReal(model.getValue());
            //              options.setSpecular(v);
            //              val = DecFormatter.twoDecimal(v);
            //            }
          }
          String result = title + StringUtil.COLON_AND_SPACE + val;
          SliderChangeListener.updateSliderProperties(s, result);
        });
    return s;
  }

  private int realToSlider(float value) {
    return Math.round(value * MAX_SLIDER_VALUE);
  }

  private int realToSliderPowerValue(float value) {
    return Math.round((value - 1.0f) * MAX_SLIDER_VALUE / 49.0f);
  }

  private float sliderToReal(int value) {
    return value / (float) MAX_SLIDER_VALUE;
  }

  private float sliderToRealPowerValue(int value) {
    return value / (float) MAX_SLIDER_VALUE * 49.f + 1f;
  }

  private void updateSliderText() {
    //    ambientSlider.setValue(realToSlider(ambient));
    //    String result = "Ambient" + StringUtil.COLON_AND_SPACE + DecFormatter.twoDecimal(ambient);
    //    SliderChangeListener.updateSliderProperties(ambientSlider, result);
    //
    //    diffuseSlider.setValue(realToSlider(diffuse));
    //    result = "Diffuse" + StringUtil.COLON_AND_SPACE + DecFormatter.twoDecimal(diffuse);
    //    SliderChangeListener.updateSliderProperties(diffuseSlider, result);
    //
    //    specularSlider.setValue(realToSlider(specular));
    //    result = "Specular" + StringUtil.COLON_AND_SPACE + DecFormatter.twoDecimal(specular);
    //    SliderChangeListener.updateSliderProperties(specularSlider, result);

    powerSlider.setValue(realToSliderPowerValue(specularPower));
    String result =
        "Specular Power" + StringUtil.COLON_AND_SPACE + DecFormatter.oneDecimal(specularPower);
    SliderChangeListener.updateSliderProperties(powerSlider, result);
  }

  private void updateValues(float specularPower) {
    ShadingOptions options = view3d.getRenderingLayer().getShadingOptions();
    view3d.getRenderingLayer().setEnableRepaint(false);
    //    options.setAmbient(ambient);
    //    options.setDiffuse(diffuse);
    //    options.setSpecular(specular);
    options.setSpecularPower(specularPower);
    view3d.getRenderingLayer().setEnableRepaint(true);
    view3d.getRenderingLayer().fireLayerChanged();
  }
}
