package AIProject;

public class Population {
	protected Child[] childs;
	private int size;

	public Population(int popsize) {
		childs = new Child[popsize];
		size = popsize;
	}

	public void reproduce(Brain b, int steps) {
		if (b != null) {
			childs[0] = new Child(b);
			for (int i = 1; i < size; i++) {
				childs[i] = new Child(b);
				childs[i].getBrain().mutate();
			}
		}else
			for (int i = 1; i < size; i++) {
				childs[i] = new Child(steps);
				childs[i].getBrain().mutate();
			}
		
	}

}
