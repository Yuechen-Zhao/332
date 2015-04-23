
public class VersionOne {
	public Rectangle rectangle;
	public float total;
	public CensusData data;
	public int x;
	public int y;
	
	public VersionOne(CensusData data, int x, int y) {
		this.data = data;
		this.x = x;
		this.y = y;
		
		float maxLat = data.data[0].latitude;
		float minLat = data.data[0].latitude;
		float maxLon = data.data[0].longitude;
		float minLon = data.data[0].longitude;
		total = 0;
		for(int i = 1; i < data.data_size; i++) {
			total += data.data[i].population;
			maxLat = Math.max(maxLat, data.data[i].latitude);
			minLat = Math.min(minLat, data.data[i].latitude);
			maxLon = Math.max(maxLon, data.data[i].longitude);
			minLon = Math.min(minLon, data.data[i].longitude);
		}
		
		rectangle = new Rectangle(minLat, maxLat, maxLon, minLon);
	}
	
	public int population(int l, int r, int t, int b) {
		int population = 0;
		float column = (rectangle.right - rectangle.left) / x;
		float row = (rectangle.top - rectangle.bottom) / y;
		float left = l * column;
		float right = r * column;
		float top = t * row;
		float bottom = b * row;
		for(CensusGroup group : data.data) {
			if(group.latitude >= left && group.latitude <= right && 
					group.longitude >= bottom && group.longitude <= top) {
				population += group.population;
			}
		}
		return population;
	}
}
