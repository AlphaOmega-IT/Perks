package de.saltyfearz.perks.database;

public interface DatabaseStruct {
	void considerUpdating();

	void invalidate();

	void update();
}
