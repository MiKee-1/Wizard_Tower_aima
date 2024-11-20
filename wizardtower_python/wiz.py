import heapq
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

class Tower:
    def __init__(self, rows: int, cols: int, potions: int, creatures: int, start_x: int, start_y: int, grid: List[List[str]], use_variant: bool):
        self.rows = rows
        self.cols = cols
        self.start_x = start_x
        self.start_y = start_y
        self.grid = grid
        self.use_variant = use_variant  

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

    def get_actions(self, state: TowerState) -> List[str]:
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

    def get_result(self, state: TowerState, action: str) -> TowerState:
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

    def is_goal(self, state: TowerState) -> bool:
        return not state.remaining_creatures and self.grid[state.x][state.y] == 'P'

    def get_step_cost(self, state: TowerState, action: str, next_state: TowerState) -> float:
        if self.use_variant and self.grid[next_state.x][next_state.y] == 'C' and not state.has_potion:
            return 2.0
        return 1.0

    def is_valid_position(self, x: int, y: int) -> bool:
        return 0 <= x < self.rows and 0 <= y < self.cols

def a_star_search(tower: Tower) -> List[str]:
    initial_state = tower.get_initial_state()
    frontier = [(0, initial_state, [])]
    visited = set()

    while frontier:
        _, state, actions = heapq.heappop(frontier)
        if state in visited:
            continue
        visited.add(state)

        if tower.is_goal(state):
            return actions

        for action in tower.get_actions(state):
            new_state = tower.get_result(state, action)
            new_cost = len(actions) + tower.get_step_cost(state, action, new_state)
            heapq.heappush(frontier, (new_cost, new_state, actions + [action]))

    return []

def main():
    tower = Tower.load_from_file("iwt.txt")

    solution = a_star_search(tower)
    print("Steps to solve the problem:")
    for action in solution:
        print(action)
    print("Number of steps:", len(solution))

if __name__ == "__main__":
    main()
