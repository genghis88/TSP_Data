package hps.nyu.fa14;

public class MirrorXYPointTranslator extends AbstractPointTranslator {

	private final Bounds _bounds;
	
	private final AbstractPointTranslator _other;
	
	public MirrorXYPointTranslator(Bounds bounds, AbstractPointTranslator other) {
		_bounds = bounds;
		_other = other;
	}
	
	@Override
	public Point getDestPoint(Point sourcePoint){
		if(_other != null){
			sourcePoint = _other.getDestPoint(sourcePoint);
		}
		Point destPoint = new Point();
		
		destPoint.x = _bounds.xMax - sourcePoint.x;
		destPoint.y = _bounds.yMax - sourcePoint.y; 
		return destPoint;
	}
	
	@Override
	public Point getSourcePoint(Point destPoint){
		
		Point sourcePoint = new Point();
		
		sourcePoint.x = _bounds.xMax - destPoint.x;
		sourcePoint.y = _bounds.yMax - destPoint.y;

		if(_other != null){
			sourcePoint = _other.getSourcePoint(sourcePoint);
		}
		return sourcePoint;
	}
}
