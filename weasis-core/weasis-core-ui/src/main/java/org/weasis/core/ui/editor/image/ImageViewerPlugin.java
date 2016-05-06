/*******************************************************************************
 * Copyright (c) 2010 Nicolas Roduit.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nicolas Roduit - initial API and implementation
 ******************************************************************************/
// Placed in public domain by Dmitry Olshansky, 2006
package org.weasis.core.ui.editor.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.plaf.PanelUI;

import org.weasis.core.api.gui.util.ActionState;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.gui.util.ComboItemListener;
import org.weasis.core.api.gui.util.Filter;
import org.weasis.core.api.gui.util.WinUtil;
import org.weasis.core.api.image.GridBagLayoutModel;
import org.weasis.core.api.image.LayoutConstraints;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.ui.Messages;
import org.weasis.core.ui.editor.SeriesViewerEvent;
import org.weasis.core.ui.editor.SeriesViewerEvent.EVENT;
import org.weasis.core.ui.editor.SeriesViewerListener;
import org.weasis.core.ui.graphic.AbstractDragGraphic;
import org.weasis.core.ui.graphic.BasicGraphic;
import org.weasis.core.ui.graphic.Graphic;
import org.weasis.core.ui.graphic.model.AbstractLayerModel;
import org.weasis.core.ui.pref.Monitor;
import org.weasis.core.ui.util.MouseEventDouble;

public abstract class ImageViewerPlugin<E extends ImageElement> extends ViewerPlugin<E> {

    // A model must have at least one view that inherited of DefaultView2d
    public static final Class view2dClass = DefaultView2d.class;
    public static final GridBagLayoutModel VIEWS_1x1 = new GridBagLayoutModel("1x1", //$NON-NLS-1$
        String.format(Messages.getString("ImageViewerPlugin.1"), "1x1"), 1, 1, view2dClass.getName(), //$NON-NLS-1$ //$NON-NLS-2$
        new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout1x1.png"))); //$NON-NLS-1$
    public static final GridBagLayoutModel VIEWS_2x1 = new GridBagLayoutModel("2x1", //$NON-NLS-1$
        String.format(Messages.getString("ImageViewerPlugin.2"), "2x1"), 2, 1, view2dClass.getName(), //$NON-NLS-1$ //$NON-NLS-2$
        new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout2x1.png"))); //$NON-NLS-1$
    public static final GridBagLayoutModel VIEWS_1x2 = new GridBagLayoutModel("1x2", //$NON-NLS-1$
        String.format(Messages.getString("ImageViewerPlugin.2"), "1x2"), 1, 2, view2dClass.getName(), //$NON-NLS-1$ //$NON-NLS-2$
        new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout1x2.png"))); //$NON-NLS-1$
    public static final GridBagLayoutModel VIEWS_2x2_f2 =
        new GridBagLayoutModel(ImageViewerPlugin.class.getResourceAsStream("/config/layoutModel2x2_f2.xml"), //$NON-NLS-1$
            "layout_c2x1", Messages.getString("ImageViewerPlugin.layout_c2x1"), //$NON-NLS-1$ //$NON-NLS-2$
            new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout2x2_f2.png"))); //$NON-NLS-1$
    public static final GridBagLayoutModel VIEWS_2_f1x2 =
        new GridBagLayoutModel(ImageViewerPlugin.class.getResourceAsStream("/config/layoutModel2_f1x2.xml"), //$NON-NLS-1$
            "layout_c1x2", Messages.getString("ImageViewerPlugin.layout_c1x2"), //$NON-NLS-1$ //$NON-NLS-2$
            new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout2_f1x2.png"))); //$NON-NLS-1$
    public static final GridBagLayoutModel VIEWS_2x2 = new GridBagLayoutModel("2x2", //$NON-NLS-1$
        String.format(Messages.getString("ImageViewerPlugin.2"), "2x2"), 2, 2, view2dClass.getName(), //$NON-NLS-1$ //$NON-NLS-2$
        new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout2x2.png"))); //$NON-NLS-1$
    public static final GridBagLayoutModel VIEWS_3x2 = new GridBagLayoutModel("3x2", //$NON-NLS-1$
        String.format(Messages.getString("ImageViewerPlugin.2"), "3x2"), 3, 2, view2dClass.getName(), //$NON-NLS-1$ //$NON-NLS-2$
        new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout3x2.png"))); //$NON-NLS-1$
    public static final GridBagLayoutModel VIEWS_3x3 = new GridBagLayoutModel("3x3", //$NON-NLS-1$
        String.format(Messages.getString("ImageViewerPlugin.2"), "3x3"), 3, 3, view2dClass.getName(), //$NON-NLS-1$ //$NON-NLS-2$
        new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout3x3.png"))); //$NON-NLS-1$
    public static final GridBagLayoutModel VIEWS_4x3 = new GridBagLayoutModel("4x3", //$NON-NLS-1$
        String.format(Messages.getString("ImageViewerPlugin.2"), "4x3"), 4, 3, view2dClass.getName(), //$NON-NLS-1$ //$NON-NLS-2$
        new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout4x3.png"))); //$NON-NLS-1$
    public static final GridBagLayoutModel VIEWS_4x4 = new GridBagLayoutModel("4x4", //$NON-NLS-1$
        String.format(Messages.getString("ImageViewerPlugin.2"), "4x4"), 4, 4, view2dClass.getName(), //$NON-NLS-1$ //$NON-NLS-2$
        new ImageIcon(ImageViewerPlugin.class.getResource("/icon/22x22/layout4x4.png"))); //$NON-NLS-1$

    /**
     * The current focused <code>ImagePane</code>. The default is 0.
     */

    protected DefaultView2d<E> selectedImagePane = null;
    /**
     * The array of display panes located in this image view panel.
     */

    protected final ArrayList<DefaultView2d<E>> view2ds;
    protected final ArrayList<Component> components;

    protected SynchView synchView = SynchView.NONE;

    protected final ImageViewerEventManager<E> eventManager;
    protected final JPanel grid;
    protected GridBagLayoutModel layoutModel;

    private final MouseHandler mouseHandler;

    public ImageViewerPlugin(ImageViewerEventManager<E> eventManager, String PluginName) {
        this(eventManager, VIEWS_1x1, PluginName, null, null, null);
    }

    public ImageViewerPlugin(ImageViewerEventManager<E> eventManager, GridBagLayoutModel layoutModel, String uid,
        String pluginName, Icon icon, String tooltips) {
        super(uid, pluginName, icon, tooltips);
        if (eventManager == null) {
            throw new IllegalArgumentException("EventManager cannot be null"); //$NON-NLS-1$
        }
        this.eventManager = eventManager;
        view2ds = new ArrayList<DefaultView2d<E>>();
        components = new ArrayList<Component>();
        grid = new JPanel();
        // For having a black background with any Look and Feel
        grid.setUI(new PanelUI() {
        });
        grid.setBackground(Color.BLACK);
        grid.setFocusCycleRoot(true);
        grid.setLayout(new GridBagLayout());
        add(grid, BorderLayout.CENTER);

        setLayoutModel(layoutModel);
        this.mouseHandler = new MouseHandler();
        grid.addMouseListener(mouseHandler);
        grid.addMouseMotionListener(mouseHandler);
    }

    /**
     * Returns true if type is instance of defaultClass. This operation is delegated in each bundle to be sure all
     * classes are visible.
     *
     * @param defaultClass
     * @param type
     * @return
     */
    public abstract boolean isViewType(Class defaultClass, String type);

    public abstract int getViewTypeNumber(GridBagLayoutModel layout, Class defaultClass);

    public abstract DefaultView2d<E> createDefaultView(String classType);

    public abstract Component createUIcomponent(String clazz);

    public DefaultView2d<E> getSelectedImagePane() {
        return selectedImagePane;
    }

    /**
     * Get the layout of this panel.
     *
     * @return the layoutModel
     */
    public GridBagLayoutModel getLayoutModel() {
        return layoutModel;
    }

    public GridBagLayoutModel getOriginalLayoutModel() {
        // Get the non clone layout from the list
        ActionState layout = eventManager.getAction(ActionW.LAYOUT);
        if (layout instanceof ComboItemListener) {
            for (Object element : ((ComboItemListener) layout).getAllItem()) {
                if (element instanceof GridBagLayoutModel) {
                    GridBagLayoutModel gbm = (GridBagLayoutModel) element;
                    if ((layoutModel.getIcon() != null && gbm.getIcon() == layoutModel.getIcon())
                        || layoutModel.toString().equals(gbm.toString())) {
                        return gbm;
                    }
                }
            }
        }
        return layoutModel;
    }

    @Override
    public void addSeries(MediaSeries<E> sequence) {
        // TODO set series in specific place and if does not exist in
        // the first free place
        if (sequence != null && selectedImagePane != null) {
            if (SynchData.Mode.Tile.equals(synchView.getSynchData().getMode())) {
                selectedImagePane.setSeries(sequence, null);
                updateTileOffset();
                return;
            }
            DefaultView2d<E> viewPane = getSelectedImagePane();
            if (viewPane != null) {
                viewPane.setSeries(sequence);
                viewPane.repaint();

                // Set selection to the next view
                setSelectedImagePane(getNextSelectedImagePane());
            }
        }
    }

    @Override
    public void removeSeries(MediaSeries<E> series) {
        if (series != null) {
            for (int i = 0; i < view2ds.size(); i++) {
                DefaultView2d<E> v = view2ds.get(i);
                if (v.getSeries() == series) {
                    v.setSeries(null, null);
                }
            }
        }
    }

    @Override
    public List<MediaSeries<E>> getOpenSeries() {
        List<MediaSeries<E>> list = new ArrayList<MediaSeries<E>>();
        for (DefaultView2d<E> v : view2ds) {
            MediaSeries<E> s = v.getSeries();
            if (s != null) {
                list.add(s);
            }
        }
        return list;
    }

    public void changeLayoutModel(GridBagLayoutModel layoutModel) {
        ActionState layout = eventManager.getAction(ActionW.LAYOUT);
        if (layout instanceof ComboItemListener) {
            ((ComboItemListener) layout).setSelectedItem(layoutModel);
        }
    }

    /**
     * Set a layout to this view panel. The layout is defined by the provided number corresponding the layout definition
     * in the property file.
     */

    protected synchronized void setLayoutModel(GridBagLayoutModel layoutModel) {
        this.layoutModel = layoutModel == null ? VIEWS_1x1 : layoutModel;
        try {
            this.layoutModel = (GridBagLayoutModel) this.layoutModel.clone();
        } catch (CloneNotSupportedException e1) {
            e1.printStackTrace();
        }
        grid.removeAll();
        // Keep views containing images
        ArrayList<DefaultView2d<E>> oldViews = new ArrayList<DefaultView2d<E>>();
        for (DefaultView2d<E> v : view2ds) {
            if (v.getSeries() != null && v.getImage() != null) {
                oldViews.add(v);
            } else {
                v.disposeView();
            }
        }
        view2ds.clear();

        int nbview = getViewTypeNumber(layoutModel, view2dClass);
        if (oldViews.size() > nbview) {
            for (int i = oldViews.size() - 1; i >= nbview; i--) {
                oldViews.remove(i).disposeView();
            }
        }
        for (Component c : components) {
            if (c instanceof SeriesViewerListener) {
                eventManager.removeSeriesViewerListener((SeriesViewerListener) c);
            }
        }
        components.clear();

        final LinkedHashMap<LayoutConstraints, Component> elements = this.layoutModel.getConstraints();
        Iterator<LayoutConstraints> enumVal = elements.keySet().iterator();
        while (enumVal.hasNext()) {
            LayoutConstraints e = enumVal.next();
            boolean typeView2d = isViewType(view2dClass, e.getType());
            if (typeView2d) {
                DefaultView2d<E> oldView;
                if (oldViews.size() > 0) {
                    oldView = oldViews.remove(0);
                } else {
                    oldView = createDefaultView(e.getType());
                    oldView.registerDefaultListeners();
                }
                view2ds.add(oldView);
                elements.put(e, oldView);
                grid.add(oldView, e);
                if (oldView.getSeries() != null) {
                    oldView.getSeries().setOpen(true);
                }
            } else {
                Component component = createUIcomponent(e.getType());
                if (component != null) {
                    if (component instanceof JComponent) {
                        ((JComponent) component).setOpaque(true);
                    }
                    components.add(component);
                    elements.put(e, component);
                    grid.add(component, e);
                }
            }
        }
        grid.revalidate();

        if (view2ds.size() > 0) {
            selectedImagePane = view2ds.get(0);

            MouseActions mouseActions = eventManager.getMouseActions();
            boolean tiledMode = SynchData.Mode.Tile.equals(synchView.getSynchData().getMode());
            for (int i = 0; i < view2ds.size(); i++) {
                DefaultView2d<E> v = view2ds.get(i);
                // Close lens because update does not work
                v.closeLens();
                if (tiledMode) {
                    v.setTileOffset(i);
                    v.setSeries(selectedImagePane.getSeries(), null);
                }
                v.enableMouseAndKeyListener(mouseActions);
            }
            Graphic graphic = null;
            ActionState action = eventManager.getAction(ActionW.DRAW_MEASURE);
            if (action instanceof ComboItemListener) {
                graphic = (Graphic) ((ComboItemListener) action).getSelectedItem();
            }
            setDrawActions(graphic);
            selectedImagePane.setSelected(true);
            eventManager.updateComponentsListener(selectedImagePane);
            if (selectedImagePane.getSeries() instanceof Series) {
                eventManager.fireSeriesViewerListeners(new SeriesViewerEvent(this, selectedImagePane.getSeries(),
                    selectedImagePane.getImage(), EVENT.LAYOUT));
            }
        }
    }

    public void replaceView(DefaultView2d<E> oldView2d, DefaultView2d<E> newView2d) {
        if (oldView2d != null && newView2d != null) {
            grid.removeAll();
            final LinkedHashMap<LayoutConstraints, Component> elements = this.layoutModel.getConstraints();
            Iterator<Entry<LayoutConstraints, Component>> enumVal = elements.entrySet().iterator();
            while (enumVal.hasNext()) {
                Entry<LayoutConstraints, Component> element = enumVal.next();

                if (element.getValue() == oldView2d) {
                    if (selectedImagePane == oldView2d) {
                        selectedImagePane = newView2d;
                    }
                    oldView2d.disposeView();
                    int index = view2ds.indexOf(oldView2d);
                    if (index >= 0) {
                        view2ds.set(index, newView2d);
                    }
                    elements.put(element.getKey(), newView2d);
                    grid.add(newView2d, element.getKey());
                    if (newView2d.getSeries() != null) {
                        newView2d.getSeries().setOpen(true);
                    }
                } else {
                    grid.add(element.getValue(), element.getKey());
                }
            }
            grid.revalidate();

            if (view2ds.size() > 0) {
                if (selectedImagePane == null) {
                    selectedImagePane = view2ds.get(0);
                }
                MouseActions mouseActions = eventManager.getMouseActions();
                boolean tiledMode = SynchData.Mode.Tile.equals(synchView);
                for (int i = 0; i < view2ds.size(); i++) {
                    DefaultView2d<E> v = view2ds.get(i);
                    // Close lens because update does not work
                    v.closeLens();
                    if (tiledMode) {
                        v.setTileOffset(i);
                        v.setSeries(selectedImagePane.getSeries(), null);
                    }
                    v.enableMouseAndKeyListener(mouseActions);
                }
                Graphic graphic = null;
                ActionState action = eventManager.getAction(ActionW.DRAW_MEASURE);
                if (action instanceof ComboItemListener) {
                    graphic = (Graphic) ((ComboItemListener) action).getSelectedItem();
                }
                setDrawActions(graphic);
                selectedImagePane.setSelected(true);
                eventManager.updateComponentsListener(selectedImagePane);
            }
        }
    }

    public void setSelectedImagePaneFromFocus(DefaultView2d<E> defaultView2d) {
        setSelectedImagePane(defaultView2d);
    }

    public void setSelectedImagePane(DefaultView2d<E> defaultView2d) {
        if (this.selectedImagePane != null && this.selectedImagePane.getSeries() != null) {
            this.selectedImagePane.getSeries().setSelected(false, null);
            this.selectedImagePane.getSeries().setFocused(false);
        }
        if (defaultView2d != null && defaultView2d.getSeries() != null) {
            defaultView2d.getSeries().setSelected(true, defaultView2d.getImage());
            defaultView2d.getSeries().setFocused(eventManager.getSelectedView2dContainer() == this);
        }

        boolean newView = this.selectedImagePane != defaultView2d && defaultView2d != null;
        if (newView) {
            if (this.selectedImagePane != null) {
                this.selectedImagePane.setSelected(false);
            }
            defaultView2d.setSelected(true);
            this.selectedImagePane = defaultView2d;
            eventManager.updateComponentsListener(defaultView2d);
        }
        if (newView && defaultView2d.getSeries() instanceof Series) {
            eventManager.fireSeriesViewerListeners(
                new SeriesViewerEvent(this, selectedImagePane.getSeries(), selectedImagePane.getImage(), EVENT.SELECT));
        }
        eventManager.fireSeriesViewerListeners(new SeriesViewerEvent(this,
            defaultView2d == null ? null : defaultView2d.getSeries(), null, EVENT.SELECT_VIEW));
    }

    public void resetMaximizedSelectedImagePane(final DefaultView2d<E> defaultView2d) {
        if (grid.getComponentCount() == 1) {
            Dialog fullscreenDialog = WinUtil.getParentDialog(grid);
            if (fullscreenDialog != null
                && fullscreenDialog.getTitle().equals(Messages.getString("ImageViewerPlugin.fullscreen"))) { //$NON-NLS-1$
                maximizedSelectedImagePane(defaultView2d, null);
            }
        }
    }

    public void maximizedSelectedImagePane(final DefaultView2d<E> defaultView2d, MouseEvent evt) {
        final LinkedHashMap<LayoutConstraints, Component> elements = layoutModel.getConstraints();
        // Prevent conflict with double click for stopping to draw a graphic (like polyline)
        List<AbstractDragGraphic> selGraphics = defaultView2d.getLayerModel().getSelectedDragableGraphics();
        if (selGraphics != null) {
            for (AbstractDragGraphic g : selGraphics) {
                if (g.getHandlePointTotalNumber() == BasicGraphic.UNDEFINED) {
                    return;
                }
            }
        }
        if (evt != null) {
            // Do not maximize when click hits a graphic
            MouseEventDouble mouseEvt = new MouseEventDouble(evt);
            mouseEvt.setImageCoordinates(defaultView2d.getImageCoordinatesFromMouse(evt.getX(), evt.getY()));
            Graphic firstGraphicIntersecting = defaultView2d.getLayerModel().getFirstGraphicIntersecting(mouseEvt);
            if (firstGraphicIntersecting != null) {
                return;
            }
        }

        for (DefaultView2d<E> v : view2ds) {
            v.removeFocusListener(v);
        }

        String titleDialog = Messages.getString("ImageViewerPlugin.fullscreen"); //$NON-NLS-1$
        Dialog fullscreenDialog = WinUtil.getParentDialog(grid);
        // Handle the case when the dialog is a detached window and not the fullscreen window.
        final boolean detachedWindow = fullscreenDialog != null && !titleDialog.equals(fullscreenDialog.getTitle());

        grid.removeAll();
        if (detachedWindow || fullscreenDialog == null) {
            remove(grid);
            Iterator<Entry<LayoutConstraints, Component>> enumVal = elements.entrySet().iterator();
            while (enumVal.hasNext()) {
                Entry<LayoutConstraints, Component> entry = enumVal.next();
                if (entry.getValue().equals(defaultView2d)) {
                    GridBagConstraints c = (GridBagConstraints) entry.getKey().clone();
                    c.weightx = 1.0;
                    c.weighty = 1.0;
                    grid.add(defaultView2d, c);
                    defaultView2d.addFocusListener(defaultView2d);
                    break;
                }
            }
            Dialog oldDialog = fullscreenDialog;
            Frame frame = WinUtil.getParentFrame(this);
            fullscreenDialog =
                new JDialog(detachedWindow ? oldDialog : frame, titleDialog, ModalityType.APPLICATION_MODAL);
            fullscreenDialog.add(grid, BorderLayout.CENTER);
            fullscreenDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    maximizedSelectedImagePane(defaultView2d, null);
                }
            });

            if (!detachedWindow && (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                fullscreenDialog.setBounds(frame.getBounds());
                fullscreenDialog.setVisible(true);
            } else {
                Monitor monitor = Monitor.getMonitor(
                    detachedWindow ? oldDialog.getGraphicsConfiguration() : frame.getGraphicsConfiguration());
                if (monitor != null) {
                    fullscreenDialog.setBounds(monitor.getFullscreenBounds());
                    fullscreenDialog.setVisible(true);
                }
            }

        } else {
            Iterator<Entry<LayoutConstraints, Component>> enumVal = elements.entrySet().iterator();
            while (enumVal.hasNext()) {
                Entry<LayoutConstraints, Component> entry = enumVal.next();
                grid.add(entry.getValue(), entry.getKey());
            }
            for (DefaultView2d<E> v : view2ds) {
                v.addFocusListener(v);
            }

            fullscreenDialog.removeAll();
            fullscreenDialog.dispose();
            add(grid, BorderLayout.CENTER);
        }

        defaultView2d.requestFocusInWindow();
    }

    public synchronized void setDrawActions(Graphic graphic) {
        for (DefaultView2d<E> v : getImagePanels()) {
            AbstractLayerModel model = v.getLayerModel();
            if (model != null) {
                model.setCreateGraphic(graphic);
            }
        }
    }

    /** Return the image in the image display panel. */

    public E getImage(int i) {
        if (i >= 0 && i < view2ds.size()) {
            return view2ds.get(i).getImage();
        }
        return null;
    }

    /** Return all the <code>ImagePanel</code>s. */

    public ArrayList<DefaultView2d<E>> getImagePanels() {
        return getImagePanels(false);
    }

    public ArrayList<DefaultView2d<E>> getImagePanels(boolean selectedImagePaneLast) {
        ArrayList<DefaultView2d<E>> viewList = new ArrayList<DefaultView2d<E>>(view2ds);
        if (selectedImagePaneLast) {
            DefaultView2d<E> selectedView = getSelectedImagePane();

            if (selectedView != null && viewList.size() > 1) {
                viewList.remove(selectedView);
                viewList.add(selectedView);
            }
            return viewList;
        }
        return viewList;
    }

    public DefaultView2d<E> getNextSelectedImagePane() {
        for (int i = 0; i < view2ds.size() - 1; i++) {
            if (view2ds.get(i) == selectedImagePane) {
                return view2ds.get(i + 1);
            }
        }
        return selectedImagePane;
    }

    public abstract List<SynchView> getSynchList();

    public abstract List<GridBagLayoutModel> getLayoutList();

    public boolean isContainingView(DefaultView2d<E> view2DPane) {
        for (DefaultView2d<E> v : view2ds) {
            if (v == view2DPane) {
                return true;
            }
        }
        return false;
    }

    public SynchView getSynchView() {
        return synchView;
    }

    public void setSynchView(SynchView synchView) {
        this.synchView = synchView;
        updateTileOffset();
        eventManager.updateAllListeners(this, synchView);
    }

    public void updateTileOffset() {
        if (SynchData.Mode.Tile.equals(synchView.getSynchData().getMode()) && selectedImagePane != null) {
            MediaSeries<E> series = null;
            DefaultView2d<E> selectedView = selectedImagePane;
            if (selectedImagePane.getSeries() != null) {
                series = selectedImagePane.getSeries();
            } else {
                for (DefaultView2d<E> v : view2ds) {
                    if (v.getSeries() != null) {
                        series = v.getSeries();
                        selectedView = v;
                        break;
                    }
                }
            }
            if (series != null) {
                int limit = series.size((Filter<E>) selectedView.getActionValue(ActionW.FILTERED_SERIES.cmd()));
                for (int i = 0; i < view2ds.size(); i++) {
                    DefaultView2d<E> v = view2ds.get(i);
                    if (i < limit) {
                        v.getLayerModel().deleteAllGraphics();
                        v.setTileOffset(i);
                        v.setSeries(series, null);
                    } else {
                        v.setSeries(null, null);
                    }
                }
            }
        } else {
            for (DefaultView2d<E> v : view2ds) {
                v.setTileOffset(0);
            }
        }
    }

    public synchronized void setMouseActions(MouseActions mouseActions) {
        if (mouseActions == null) {
            for (DefaultView2d<E> v : view2ds) {
                v.disableMouseAndKeyListener();
                // Let the possibility to get the focus
                v.iniDefaultMouseListener();
            }
        } else {
            for (DefaultView2d<E> v : view2ds) {
                v.enableMouseAndKeyListener(mouseActions);
            }
        }
    }

    public GridBagLayoutModel getBestDefaultViewLayout(int size) {
        if (size <= 1) {
            return VIEWS_1x1;
        }
        ActionState layout = eventManager.getAction(ActionW.LAYOUT);
        if (layout instanceof ComboItemListener) {
            Object[] list = ((ComboItemListener) layout).getAllItem();
            for (Object m : list) {
                if (m instanceof GridBagLayoutModel) {
                    if (getViewTypeNumber((GridBagLayoutModel) m, view2dClass) >= size) {
                        return (GridBagLayoutModel) m;
                    }
                }
            }
        }

        return VIEWS_4x4;
    }

    public GridBagLayoutModel getViewLayout(String title) {
        if (title != null) {
            ActionState layout = eventManager.getAction(ActionW.LAYOUT);
            if (layout instanceof ComboItemListener) {
                Object[] list = ((ComboItemListener) layout).getAllItem();
                for (Object m : list) {
                    if ((m instanceof GridBagLayoutModel && title.equals(((GridBagLayoutModel) m).getId()))) {
                        return (GridBagLayoutModel) m;
                    }
                }
            }
        }
        return VIEWS_1x1;
    }

    public void addSeriesList(List<MediaSeries<E>> seriesList, boolean bestDefaultLayout) {
        if (seriesList != null && seriesList.size() > 0) {
            if (SynchData.Mode.Tile.equals(synchView.getSynchData().getMode())) {
                addSeries(seriesList.get(0));
                return;
            }
            setSelectedAndGetFocus();
            if (bestDefaultLayout) {
                changeLayoutModel(getBestDefaultViewLayout(seriesList.size()));

                // If the layout is larger than the list of series, clean other views.
                if (view2ds.size() > seriesList.size()) {
                    setSelectedImagePane(view2ds.get(seriesList.size()));
                    for (int i = seriesList.size(); i < view2ds.size(); i++) {
                        DefaultView2d<E> viewPane = getSelectedImagePane();
                        if (viewPane != null) {
                            viewPane.setSeries(null, null);
                        }
                        getNextSelectedImagePane();
                    }
                }
                if (view2ds.size() > 0) {
                    setSelectedImagePane(view2ds.get(0));
                }
                for (MediaSeries mediaSeries : seriesList) {
                    addSeries(mediaSeries);
                }
            } else {
                int emptyView = 0;
                for (DefaultView2d v : view2ds) {
                    if (v.getSeries() == null) {
                        emptyView++;
                    }
                }
                if (emptyView < seriesList.size()) {
                    changeLayoutModel(getBestDefaultViewLayout(view2ds.size() + seriesList.size()));
                }
                int index = 0;
                for (DefaultView2d v : view2ds) {
                    if (v.getSeries() == null && index < seriesList.size()) {
                        setSelectedImagePane(v);
                        addSeries(seriesList.get(index));
                        index++;
                    }
                }
            }
            // setSelected(true);
            repaint();
        }
    }

    class MouseHandler extends MouseAdapter {
        private Point pickPoint = null;
        private Point point = null;
        private boolean splitVertical = false;
        private final ArrayList<ImageViewerPlugin.DragLayoutElement> list =
            new ArrayList<ImageViewerPlugin.DragLayoutElement>();

        @Override
        public void mousePressed(MouseEvent e) {
            pickPoint = e.getPoint();
            point = null;
            list.clear();
            Iterator<Entry<LayoutConstraints, Component>> enumVal =
                ImageViewerPlugin.this.layoutModel.getConstraints().entrySet().iterator();
            Entry<LayoutConstraints, Component> entry = null;
            while (enumVal.hasNext()) {
                entry = enumVal.next();
                Component c = entry.getValue();
                if (c != null) {
                    Rectangle rect = c.getBounds();
                    if (Math.abs(rect.x - pickPoint.x) <= LayoutConstraints.SPACE
                        && (pickPoint.y >= rect.y && pickPoint.y <= rect.y + rect.height) && entry.getKey().gridx > 0) {
                        splitVertical = true;
                        point = new Point(entry.getKey().gridx, entry.getKey().gridy);
                        break;
                    }

                    else if (Math.abs(rect.y - pickPoint.y) <= LayoutConstraints.SPACE
                        && (pickPoint.x >= rect.x && pickPoint.x <= rect.x + rect.width) && entry.getKey().gridy > 0) {
                        splitVertical = false;
                        point = new Point(entry.getKey().gridx, entry.getKey().gridy);
                        break;
                    }
                }
            }
            if (point != null) {
                enumVal = ImageViewerPlugin.this.layoutModel.getConstraints().entrySet().iterator();
                while (enumVal.hasNext()) {
                    entry = enumVal.next();
                    Component c = entry.getValue();
                    if (c != null) {
                        list.add(new DragLayoutElement(entry.getKey(), c));
                    }
                }

                Rectangle b = grid.getBounds();
                double totalWidth = b.getWidth();
                double totalHeight = b.getHeight();

                for (DragLayoutElement el : list) {
                    el.originalConstraints.weightx = el.originalBound.width / totalWidth;
                    el.originalConstraints.weighty = el.originalBound.height / totalHeight;
                    el.constraints.weightx = el.originalConstraints.weightx;
                    el.constraints.weighty = el.originalConstraints.weighty;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseevent) {
            pickPoint = null;
            point = null;
            list.clear();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int mods = e.getModifiers();
            if (pickPoint != null && point != null && list.size() > 1 && (mods & InputEvent.BUTTON1_MASK) != 0) {
                Point p = e.getPoint();
                if (splitVertical) {
                    int dx = p.x - pickPoint.x;
                    int limitdx = dx;
                    for (DragLayoutElement element : list) {
                        LayoutConstraints key = element.getConstraints();
                        if (key.gridx == point.x) {
                            int width = element.getOriginalBound().width - dx;
                            Dimension min = element.getComponent().getMinimumSize();
                            int minsize = min == null ? 50 : min.width;
                            if (width < minsize) {
                                limitdx = dx - (minsize - width);
                            }
                        } else if (key.gridx + key.gridwidth == point.x) {
                            int width = element.getOriginalBound().width + dx;
                            Dimension min = element.getComponent().getMinimumSize();
                            int minsize = min == null ? 50 : min.width;
                            if (width < minsize) {
                                limitdx = dx + (minsize - width);
                            }
                        }
                    }
                    for (DragLayoutElement element : list) {
                        LayoutConstraints key = element.getConstraints();
                        LayoutConstraints originkey = element.getOriginalConstraints();
                        if (key.gridx == point.x) {
                            key.weightx =
                                originkey.weightx - limitdx * originkey.weightx / element.getOriginalBound().width;
                        } else if (key.gridx + key.gridwidth == point.x) {
                            key.weightx =
                                originkey.weightx + limitdx * originkey.weightx / element.getOriginalBound().width;
                        }
                    }
                } else {
                    int dy = p.y - pickPoint.y;
                    int limitdy = dy;
                    for (DragLayoutElement element : list) {
                        LayoutConstraints key = element.getConstraints();
                        if (key.gridy == point.y) {
                            int height = element.getOriginalBound().height - dy;
                            Dimension min = element.getComponent().getMinimumSize();
                            int minsize = min == null ? 50 : min.height;
                            if (height < minsize) {
                                limitdy = dy - (minsize - height);
                            }
                        } else if (key.gridy + key.gridheight == point.y) {
                            int height = element.getOriginalBound().height + dy;
                            Dimension min = element.getComponent().getMinimumSize();
                            int minsize = min == null ? 50 : min.height;
                            if (height < minsize) {
                                limitdy = dy + (minsize - height);
                            }
                        }
                    }
                    for (DragLayoutElement element : list) {
                        LayoutConstraints key = element.getConstraints();
                        LayoutConstraints originkey = element.getOriginalConstraints();
                        if (key.gridy == point.y) {
                            key.weighty =
                                originkey.weighty - limitdy * originkey.weighty / element.getOriginalBound().height;
                        } else if (key.gridy + key.gridheight == point.y) {
                            key.weighty =
                                originkey.weighty + limitdy * originkey.weighty / element.getOriginalBound().height;
                        }

                    }
                }
                Rectangle b = grid.getBounds();
                double totalWidth = b.getWidth();
                double totalHeight = b.getHeight();
                grid.removeAll();
                for (DragLayoutElement element : list) {
                    Component c = element.getComponent();
                    LayoutConstraints l = element.getConstraints();
                    c.setPreferredSize(new Dimension((int) Math.round(totalWidth * l.weightx),
                        (int) Math.round(totalHeight * l.weighty)));
                    grid.add(c, l);
                }
                setCursor(Cursor.getPredefinedCursor(splitVertical ? Cursor.E_RESIZE_CURSOR : Cursor.S_RESIZE_CURSOR));
                grid.revalidate();
                grid.repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            setCursor(Cursor.getPredefinedCursor(getCursor(me)));
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            setCursor(Cursor.getDefaultCursor());
        }

        private int getCursor(MouseEvent me) {
            Point p = me.getPoint();
            Iterator<Entry<LayoutConstraints, Component>> enumVal =
                ImageViewerPlugin.this.layoutModel.getConstraints().entrySet().iterator();
            while (enumVal.hasNext()) {
                Entry<LayoutConstraints, Component> entry = enumVal.next();
                Component c = entry.getValue();
                if (c != null) {
                    Rectangle rect = c.getBounds();
                    if ((Math.abs(rect.x - p.x) <= LayoutConstraints.SPACE
                        || Math.abs(rect.x + rect.width - p.x) <= LayoutConstraints.SPACE)
                        && (p.y >= rect.y && p.y <= rect.y + rect.height)) {
                        return Cursor.E_RESIZE_CURSOR;
                    } else if ((Math.abs(rect.y - p.y) <= LayoutConstraints.SPACE
                        || Math.abs(rect.y + rect.height - p.y) <= LayoutConstraints.SPACE)
                        && (p.x >= rect.x && p.x <= rect.x + rect.width)) {
                        return Cursor.S_RESIZE_CURSOR;
                    }
                }

            }
            return Cursor.DEFAULT_CURSOR;
        }
    }

    static class DragLayoutElement {
        private final LayoutConstraints originalConstraints;
        private final Rectangle originalBound;
        private final LayoutConstraints constraints;
        private final Component component;

        public DragLayoutElement(LayoutConstraints constraints, Component component) {
            if (constraints == null || component == null) {
                throw new IllegalArgumentException("Arguments cannot be null"); //$NON-NLS-1$
            }
            this.constraints = constraints;
            this.originalConstraints = (LayoutConstraints) constraints.clone();
            this.component = component;
            this.originalBound = component.getBounds();
        }

        public LayoutConstraints getOriginalConstraints() {
            return originalConstraints;
        }

        public Rectangle getOriginalBound() {
            return originalBound;
        }

        public LayoutConstraints getConstraints() {
            return constraints;
        }

        public Component getComponent() {
            return component;
        }

    }

    public void selectLayoutPositionForAddingSeries(List<MediaSeries<E>> seriesList) {
        int nbSeriesToAdd = 1;
        if (seriesList != null) {
            nbSeriesToAdd = seriesList.size();
            if (nbSeriesToAdd < 1) {
                nbSeriesToAdd = 1;
            }
        }
        int pos = view2ds.size() - nbSeriesToAdd;
        if (pos < 0) {
            pos = 0;
        }
        setSelectedImagePane(view2ds.get(pos));
    }
}
