class Pair<T,U> {
	private T key;
	private U value;

	public Pair(){
		//
	}
	public Pair(T key){
		this.key=key;
	}
	public Pair(T key, U value){
		this.key=key;
		this.value=value;
	}

	public void set(T key, U value){
		this.key=key;
		this.value=value;
	}

	public T getKey(){
		return this.key;
	}
	public void setKey(T key){
		this.key=key;
	}

	public U getValue(){
		return this.value;
	}
	public void setValue(U value){
		this.value=value;
	}

	@Override
	public String toString(){
		return String.format("<%s,%s>",key.toString(),value.toString());
	}
}
