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

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class LightingMap extends TextureData {

  final float[] map;

  public LightingMap(int width) {
    super(width, PixelFormat.RGBA32F);
    map = new float[width * 4];
  }

  @Override
  public void init(GL2 gl2) {
    super.init(gl2);
    gl2.glActiveTexture(GL.GL_TEXTURE2);
    gl2.glBindTexture(GL.GL_TEXTURE_2D, getId());
    gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
    gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
    gl2.glTexImage2D(
        GL.GL_TEXTURE_2D,
        0,
        internalFormat,
        map.length / 4,
        height,
        0,
        format,
        type,
        Buffers.newDirectFloatBuffer(map).rewind());
  }

  @Override
  public void render(GL2 gl2) {
    update(gl2);
  }

  void update(GL2 gl2) {
    if (gl2 != null) {
      if (getId() <= 0) {
        init(gl2);
      }
      gl2.glActiveTexture(GL.GL_TEXTURE2);
      gl2.glTexImage2D(
          GL.GL_TEXTURE_2D,
          0,
          internalFormat,
          map.length / 4,
          height,
          0,
          format,
          type,
          Buffers.newDirectFloatBuffer(map).rewind());
    }
  }

  public void setAmbient(int index, float value) {
    int i = index * 4;
    if (i < map.length) {
      map[i] = value;
    }
  }

  public void setDiffuse(int index, float value) {
    int i = index * 4 + 1;
    if (i < map.length) {
      map[i] = value;
    }
  }

  public void setSpecular(int index, float value) {
    int i = index * 4 + 2;
    if (i < map.length) {
      map[i] = value;
    }
  }
}
