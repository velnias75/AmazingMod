package de.rangun.amazing.maze;

import javax.annotation.Nonnull;

public interface IPattern<M> {
	@Nonnull
	M materialAt(int x, int y, int h);
}
