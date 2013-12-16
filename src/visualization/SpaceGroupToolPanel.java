package visualization;

import interfaces.Controller;
import interfaces.View;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.*;

import org.eclipse.swt.internal.win32.BUTTON_IMAGELIST;

public class SpaceGroupToolPanel extends Panel implements ActionListener, View {

	private final Controller _controller;
	private final JToggleButton _btnVertices;
	private final JToggleButton _btnWireframe;
	private final JToggleButton _btnFace;
	private final JToggleButton _btnSpacing;
	
	private final ResourceBundle bundle = ResourceBundle.getBundle("resources.Messages");
	private static final Dimension BUTTON_SIZE = new Dimension(40, 40);
	
	public SpaceGroupToolPanel(Controller controller) {
		super();
		this._controller = controller;
		
		this.setPreferredSize(new Dimension(50, 600));
		this.setBackground(org.jzy3d.colors.ColorAWT.toAWT(SpaceGroupView.Viewport_Background));
		this.setForeground(org.jzy3d.colors.ColorAWT.toAWT(SpaceGroupView.Foregrond_Color));
		
		final BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(layout);
		
		_btnVertices = new JToggleButton(new ImageIcon(ClassLoader.getSystemResource("images/poly_vertice.png")));
		_btnVertices.setToolTipText(bundle.getString("showVertices"));
		styleButton(_btnVertices);
		this.add(_btnVertices);

		_btnWireframe = new JToggleButton(new ImageIcon(ClassLoader.getSystemResource("images/poly_wire.png")));
		_btnWireframe.setToolTipText(bundle.getString("showWireframe"));
		styleButton(_btnWireframe);
		this.add(_btnWireframe);
		
		_btnFace = new JToggleButton(new ImageIcon(ClassLoader.getSystemResource("images/poly_face.png")));
		_btnFace.setToolTipText(bundle.getString("showFaces"));
		styleButton(_btnFace);
		this.add(_btnFace);

		_btnSpacing = new JToggleButton(new ImageIcon(ClassLoader.getSystemResource("images/grid.png")));
		_btnSpacing.setToolTipText(bundle.getString("showSpacing"));
		styleButton(_btnSpacing);
		this.add(_btnSpacing);
		
		this.invalidateViewOptions();
		
		// attach action listener
		this._btnVertices.addActionListener(this);
		this._btnWireframe.addActionListener(this);
		this._btnFace.addActionListener(this);
		this._btnSpacing.addActionListener(this);
	}
	
	private static void styleButton(AbstractButton btn) {
		btn.setPreferredSize(BUTTON_SIZE);
		btn.setFocusable(false);	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _btnVertices) {
			this._controller.setViewOption(Controller.ViewOptions.ShowVertices, _btnVertices.isSelected());
		}
		else if (e.getSource() == _btnFace) {
			this._controller.setViewOption(Controller.ViewOptions.ShowFaces, _btnFace.isSelected());
		}
		else if (e.getSource() == _btnWireframe) {
			this._controller.setViewOption(Controller.ViewOptions.ShowWireframe, _btnWireframe.isSelected());
		}
		else if (e.getSource() == _btnSpacing) {
			this._controller.setViewOption(Controller.ViewOptions.ShowSpacing, _btnSpacing.isSelected());
		}
	}

	@Override
	public void invalidateView() {

	}

	@Override
	public void invalidateViewOptions() {
		this._btnVertices.setSelected(this._controller.getViewOption(Controller.ViewOptions.ShowVertices));
		this._btnWireframe.setSelected(this._controller.getViewOption(Controller.ViewOptions.ShowWireframe));
		this._btnFace.setSelected(this._controller.getViewOption(Controller.ViewOptions.ShowFaces));
		this._btnSpacing.setSelected(this._controller.getViewOption(Controller.ViewOptions.ShowSpacing));
	}
	
}
