package decaf;

public class ScopeElement {
	private String name;
	public int type;
	private int line;
	public int[] ptypes = null;
	private int size = -1;
	
	public ScopeElement (String name, int type, int line) {
		this.name = name;
		this.type = type;
		this.line = line;
	}
	
	public ScopeElement (String name, int type, int line, int size) {
		this.name = name;
		this.type = type;
		this.line = line;
		this.size = size;
	}
	
	public ScopeElement (String name, int line, int[] ptypes) {
		this.name = name;
		this.type = type;
		this.line = line;
		
		if(ptypes == null || ptypes.length == 0) {
			this.ptypes = null;
		} else {
			int n = ptypes.length;
			this.ptypes = new int[n];
			for(int i = 0; i < n; i++)
				this.ptypes[i] = ptypes[i];
		}
	}
	
	public String getLine() {
		return Integer.toString(line);
	}
	
	public int getType() {
		return type;
	}
	
	public int[] getTypes() {
		return ptypes;
	}
	
	public int getSize() {
		return size;
	}
}
