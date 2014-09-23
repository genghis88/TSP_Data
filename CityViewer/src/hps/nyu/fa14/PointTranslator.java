package hps.nyu.fa14;

public class PointTranslator extends AbstractPointTranslator {

	private final Bounds _source;
	private final Bounds _dest;

	private final Point _sourceCenter;
	private final Point _destCenter;
	
	private final double scale;
	
	public PointTranslator(Bounds source, Bounds dest, AbstractPointTranslator other) {
		_source = source;
		_dest = dest;
	
		_sourceCenter = _source.center();
		_destCenter = dest.center();
		double scale = _dest.xDiff() / _source.xDiff();
		if(Math.abs(dest.yDiff() / _source.yDiff()) < Math.abs(scale)){
			scale = dest.yDiff() / _source.yDiff();
		}
		this.scale = scale;
	}
	
	@Override
	public Point getDestPoint(Point sourcePoint){
		
		Point destPoint = new Point();
		
		destPoint.x = ((sourcePoint.x - _sourceCenter.x) * scale) + _destCenter.x; 
		destPoint.y = ((sourcePoint.y - _sourceCenter.y) * scale) + _destCenter.y; 
		return destPoint;
	}
	
	@Override
	public Point getSourcePoint(Point destPoint){
		
		Point sourcePoint = new Point();
		
		sourcePoint.x = ((destPoint.x - _destCenter.x) / scale) + _sourceCenter.x;
		sourcePoint.y = ((destPoint.y - _destCenter.y) / scale) + _sourceCenter.y;
		
		return sourcePoint;
	}
}
