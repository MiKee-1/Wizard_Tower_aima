from aima3.search import Problem, astar_search, Node
from typing import List, Tuple, Set

Point = Tuple[int, int]

class TowerState:
    def __init__(self, x: int, y: int, has_potion: bool, remaining_potions: Set[Point], remaining_creatures: Set[Point]):
        self.x = x
        self.y = y
        self.has_potion = has_potion
        self.remaining_potions = remaining_potions
        self.remaining_creatures = remaining_creatures

    def __eq__(self, other):
        return (self.x, self.y, self.has_potion, self.remaining_potions, self.remaining_creatures) == \
               (other.x, other.y, other.has_potion, other.remaining_potions, other.remaining_creatures)

    def __hash__(self):
        return hash((self.x, self.y, self.has_potion, frozenset(self.remaining_potions), frozenset(self.remaining_creatures)))

    def __lt__(self, other):
        if len(self.remaining_creatures) != len(other.remaining_creatures):
            return len(self.remaining_creatures) < len(other.remaining_creatures)
        return len(self.remaining_potions) < len(other.remaining_potions)

    def __str__(self):
        return f"State(x={self.x}, y={self.y}, has_potion={self.has_potion}, remaining_potions={len(self.remaining_potions)}, remaining_creatures={len(self.remaining_creatures)})"

class Tower(Problem):
    def __init__(self, rows: int, cols: int, potions: int, creatures: int, start_x: int, start_y: int, grid: List[List[str]], use_variant: bool):
        self.rows = rows
        self.cols = cols
        self.grid = grid
        self.use_variant = use_variant
        self.start_x=start_x
        self.start_y=start_y
        initial_state = self.get_initial_state()
        super().__init__(initial_state)

    @classmethod
    def load_from_file(cls, filename: str) -> 'Tower':
        with open(filename, 'r') as file:
            rows = int(file.readline().strip())
            cols = int(file.readline().strip())
            potions = int(file.readline().strip())
            creatures = int(file.readline().strip())
            start_x, start_y = map(int, file.readline().strip().split())
            grid = [list(file.readline().strip()) for _ in range(rows)]

        use_variant = input("Use variant? (yes/no): ").strip().lower() == "yes"
        return cls(rows, cols, potions, creatures, start_x, start_y, grid, use_variant)

    def get_initial_state(self) -> TowerState:
        remaining_potions = set()
        remaining_creatures = set()
        for i in range(self.rows):
            for j in range(self.cols):
                if self.grid[i][j] == 'M':
                    remaining_potions.add((i, j))
                elif self.grid[i][j] == 'C':
                    remaining_creatures.add((i, j))
        return TowerState(self.start_x, self.start_y, False, remaining_potions, remaining_creatures)

    def actions(self, state: TowerState) -> List[str]:
        actions = []
        self.check_move(state, state.x - 1, state.y, "UP", actions)
        self.check_move(state, state.x + 1, state.y, "DOWN", actions)
        self.check_move(state, state.x, state.y - 1, "LEFT", actions)
        self.check_move(state, state.x, state.y + 1, "RIGHT", actions)

        current_pos = (state.x, state.y)
        if not state.has_potion and current_pos in state.remaining_potions:
            actions.append("PICK_POTION")
        return actions

    def check_move(self, state: TowerState, new_x: int, new_y: int, direction: str, actions: List[str]):
        if self.is_valid_position(new_x, new_y):
            can_move = False
            if self.grid[new_x][new_y] == 'C':
                if state.has_potion or self.use_variant:
                    can_move = True
            elif self.grid[new_x][new_y] != 'B':
                can_move = True
            if can_move:
                actions.append(f"MOVE_{direction}")

    def result(self, state: TowerState, action: str) -> TowerState:
        new_x, new_y = state.x, state.y
        has_potion = state.has_potion
        remaining_potions = set(state.remaining_potions)
        remaining_creatures = set(state.remaining_creatures)

        if action == "MOVE_UP":
            new_x -= 1
        elif action == "MOVE_DOWN":
            new_x += 1
        elif action == "MOVE_LEFT":
            new_y -= 1
        elif action == "MOVE_RIGHT":
            new_y += 1
        elif action == "PICK_POTION":
            has_potion = True
            remaining_potions.remove((state.x, state.y))

        new_pos = (new_x, new_y)
        if new_pos in remaining_creatures:
            if has_potion:
                remaining_creatures.remove(new_pos)
                has_potion = False

        return TowerState(new_x, new_y, has_potion, remaining_potions, remaining_creatures)

    def goal_test(self, state: TowerState) -> bool:
        return not state.remaining_creatures and self.grid[state.x][state.y] == 'P'

    def step_cost(self, state: TowerState, action: str, next_state: TowerState) -> float:
        if self.use_variant and self.grid[next_state.x][next_state.y] == 'C' and not state.has_potion:
            return 2.0
        return 1.0

    def is_valid_position(self, x: int, y: int) -> bool:
        return 0 <= x < self.rows and 0 <= y < self.cols

    def h(self, node) -> float:
        state = node.state
        current_pos = (state.x, state.y)
        
        if state.remaining_creatures:
            nearest_creature_dist = min(abs(current_pos[0] - cx) + abs(current_pos[1] - cy) for cx, cy in state.remaining_creatures)
        else:
            nearest_creature_dist = 0
        
        if state.remaining_potions:
            nearest_potion_dist = min(abs(current_pos[0] - px) + abs(current_pos[1] - py) for px, py in state.remaining_potions)
        else:
            nearest_potion_dist = 0
        
        return nearest_creature_dist + 0.5 * nearest_potion_dist

def main():
    tower = Tower.load_from_file(input("Inserisci il percorso (assoluto o relativo) del file da utilizzare: "))

    solution_node = astar_search(tower)
    solution = solution_node.solution() if solution_node else []
    node = Node(tower.initial)
    frontier = [node]
    max_queue_size = 1
    nodes_expanded = 0
    
    print("Steps to solve the problem:")
    for action in solution:
        print(action)
    print("Number of steps:", len(solution))
    

    explored = set()

    while frontier:
        # Track max queue size
        max_queue_size = max(max_queue_size, len(frontier))

        node = min(frontier)
        frontier.remove(node)

        if tower.goal_test(node.state):
            print(f"Queue Size: {len(frontier)}")
            print(f"Max Queue Size: {max_queue_size}")
            print(f"Nodes Expanded: {nodes_expanded}")
            return node

        explored.add(node.state)
        nodes_expanded += 1

        for action in tower.actions(node.state):
            child = node.child_node(tower, action)
            if child.state not in explored and child not in frontier:
                frontier.append(child)
            elif child in frontier:
                # Optional: replace if lower cost
                existing = [n for n in frontier if n == child][0]
                if child.path_cost < existing.path_cost:
                    frontier.remove(existing)
                    frontier.append(child)

if __name__ == "__main__":
    main()
