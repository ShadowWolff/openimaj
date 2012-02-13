package org.openimaj.demos.acmmm11.presentation.slides;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JPanel;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.demos.video.VideoSIFT;


/**
 * Slide showing SIFT tracking
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 *
 */
public class SIFTTrackerSlide implements Slide, KeyListener {
	VideoSIFT vs;
	
	@Override
	public Component getComponent(int width, int height) throws IOException {
		JPanel c = new JPanel();
		c.setOpaque(false);
		c.setPreferredSize(new Dimension(width, height));
		c.setLayout(new GridBagLayout());
		
		try {
			vs = new VideoSIFT(c);
			
			for (Component cc : c.getComponents()) {
				if (cc instanceof JPanel) {
					((JPanel)cc).setOpaque( false );
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return c;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (vs != null) vs.keyTyped(e);
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (vs != null) vs.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (vs != null) vs.keyReleased(e);
	}

	@Override
	public void close() {
		if (vs != null) vs.stop();
	}

}
