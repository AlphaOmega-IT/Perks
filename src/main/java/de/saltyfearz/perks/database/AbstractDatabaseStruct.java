package de.saltyfearz.perks.database;

public abstract class AbstractDatabaseStruct implements DatabaseStruct {
	
	private int updateDelay = 30000;
	
	private long nextUpdateTimestamp = Long.MIN_VALUE;
	
	private boolean currentlyUpdating;
	
	private boolean firstUpdated;

	@Override
	public void considerUpdating() {
		if (System.currentTimeMillis() >= this.nextUpdateTimestamp) 
			update();
	}

	@Override
	public void invalidate() {
		this.nextUpdateTimestamp = Long.MIN_VALUE;
	}

	@Override
	public void update() {
		if (this.currentlyUpdating) 
			return;
		this.currentlyUpdating = true;
		try {
			doUpdate();
			this.firstUpdated = true;
			resetUpdateTimeout();
		} finally {
			this.currentlyUpdating = false;
		}
	}
	
	public boolean updatedForFirstTime() {
		return this.firstUpdated;
	}
	
	protected abstract void doUpdate();
	
	protected void setUpdateDelay(int delay) {
		this.updateDelay = delay;
	}
	
	public void resetUpdateTimeout() {
		this.nextUpdateTimestamp = System.currentTimeMillis() + this.updateDelay;
	}

}
