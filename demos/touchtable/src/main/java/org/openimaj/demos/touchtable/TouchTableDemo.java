package org.openimaj.demos.touchtable;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.connectedcomponent.ConnectedComponentLabeler;
import org.openimaj.image.connectedcomponent.ConnectedComponentLabeler.Algorithm;
import org.openimaj.image.feature.local.detector.mser.MSERFeatureGenerator;
import org.openimaj.image.feature.local.detector.mser.MSERFeatureGenerator.MSERDirection;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.processing.morphology.Close;
import org.openimaj.image.processing.watershed.Component;
import org.openimaj.image.processing.watershed.feature.MomentFeature;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.Device;
import org.openimaj.video.capture.VideoCapture;

public class TouchTableDemo implements VideoDisplayListener<MBFImage> {
	
	private static final int IMAGE_WIDTH = 160;
	private static final int IMAGE_HEIGHT = 120;
	public static final int SMALLEST_POINT_AREA = Math.max(1,(IMAGE_WIDTH*IMAGE_HEIGHT)/(320*240));
	public static final int BIGGEST_POINT_AREA = Math.max(1,(IMAGE_WIDTH*IMAGE_HEIGHT)/(30*10));
	public static final int SMALLEST_POINT_DIAMETER = (int) (IMAGE_HEIGHT/30);
	public static final int BIGGEST_POINT_DIAMETER = (int) SMALLEST_POINT_DIAMETER*2;
	private VideoCapture capture;
	private VideoDisplay<MBFImage> display;
	private ConnectedComponentLabeler labler;
	private TouchTableScreen touchTableScreen;
	private FImageBackgroundLearner backgroundLearner;
	enum DebugMode{
		DEBUG_DISPLAY,
		NONE
	}
	private DebugMode mode = DebugMode.NONE;
	private KeyListener touchTableKeyboard;
	
	public Rectangle extractionArea = new Rectangle(IMAGE_WIDTH/10,IMAGE_HEIGHT/10,IMAGE_WIDTH-(IMAGE_WIDTH/3.5f),IMAGE_HEIGHT-(IMAGE_HEIGHT/4.5f));
	private MSERFeatureGenerator mserDetector;
	
	@SuppressWarnings("unchecked")
	public TouchTableDemo() throws IOException{
		List<Device> captureDevices = VideoCapture.getVideoDevices();
		this.capture = new VideoCapture(IMAGE_WIDTH,IMAGE_HEIGHT,60,captureDevices.get(0));
		this.display = VideoDisplay.createVideoDisplay(capture);
		ConnectedComponentLabeler.DEFAULT_ALGORITHM = Algorithm.SINGLE_PASS;
		mserDetector = new MSERFeatureGenerator(MomentFeature.class);
		this.labler = new ConnectedComponentLabeler(ConnectedComponent.ConnectMode.CONNECT_4);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = ge.getScreenDevices();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(devices.length > 1){
			// Then there is a touchscreen attached
			this.touchTableScreen = new TouchTableScreen(extractionArea,new Rectangle(0,0,800,600-25));
			this.touchTableScreen.setUndecorated(true);
			this.touchTableScreen.setAlwaysOnTop(true);
			devices[1].setFullScreenWindow(this.touchTableScreen);
			this.touchTableScreen.init();
			
			
		}
		
		this.backgroundLearner = new FImageBackgroundLearner();
		this.display.addVideoListener(this);
		touchTableKeyboard = new TouchTableKeyboard(this,this.touchTableScreen);
		SwingUtilities.getRoot(this.display.getScreen()).addKeyListener(this.touchTableKeyboard);

	}
	
	public static void main(String[] args) throws IOException {
		new TouchTableDemo();
	}

	@Override
	public void afterUpdate(VideoDisplay<MBFImage> display) {
		
	}
	
	@Override
	public void beforeUpdate(MBFImage frame) {
//		Rectangle extractionArea = new Rectangle(100,100,IMAGE_WIDTH-200,IMAGE_HEIGHT-200);
		
		if(this.mode == DebugMode.DEBUG_DISPLAY) return;
		FImage grey = frame.extractROI(extractionArea).flatten();
		if(!this.backgroundLearner.ready()){
			grey.process(this.backgroundLearner);
			frame.fill(RGBColour.BLACK);
			frame.drawImage(new MBFImage(grey,grey,grey), (int)extractionArea.x, (int)extractionArea.y);
			return;
		}
		grey.addInline(this.backgroundLearner.getBackground());
		grey.threshold(0.07f);
//		grey.processInline(new OtsuThreshold());
//		if(grey.sum() > BIGGEST_POINT_AREA * 2 ){
//			this.backgroundLearner.relearn();
//			return;
//		}
		
//		List<Circle> filtered = getFilteredCircles(grey);
		List<Touch> filtered = getFilteredTouchesFast(grey);
		if(filtered.size() != 0)
		this.fireTouchEvent(filtered);
		frame.fill(RGBColour.BLACK);
		frame.drawImage(new MBFImage(grey,grey,grey), (int)extractionArea.x, (int)extractionArea.y);
	}


	private List<Touch> getFilteredTouchesFast(FImage grey) {
		Close morphClose = new Close();
		grey.processInline(morphClose);
		List<Component> comps = mserDetector.generateMSERs(grey, MSERDirection.Down);
		List<Touch> ret = new ArrayList<Touch>();
		
		for (Component component : comps) {
			int nPixels = component.size();
			if(nPixels < SMALLEST_POINT_AREA)
			{
//				if(nPixels > SMALLEST_POINT_AREA/10)
				continue;
			}
//			else if(nPixels > BIGGEST_POINT_AREA) {
//				continue;
//			}
			ret.add(new Touch(((MomentFeature) component.getFeature(0)).getCircle(10.0f)));
		}
		return ret;
	}

	private List<Touch> getFilteredTouches(FImage grey) {
		List<ConnectedComponent> comps = labler.findComponents(grey);
		
		List<Touch> filtered = new ArrayList<Touch>();
		
		double[] hw = null;
		double[] c = null;
		
		for (ConnectedComponent connectedComponent : comps) {
			int nPixels = connectedComponent.pixels.size();
			if(nPixels < SMALLEST_POINT_AREA)
			{
//				if(nPixels > SMALLEST_POINT_AREA/10)
				continue;
			}
			else if(nPixels > BIGGEST_POINT_AREA) {
				continue;
			}
			c = connectedComponent.calculateCentroid();
			hw = connectedComponent.calculateAverageHeightWidth(c);
			
			filtered.add(new Touch((float)c[0],(float)c[1],(float)Math.sqrt(hw[0]*hw[0] + hw[1]*hw[1])));
		}
		return filtered;
	}

	private void fireTouchEvent(List<Touch> filtered) {
		if(this.touchTableScreen!=null)
			this.touchTableScreen.touchEvent(filtered);
	}
}
