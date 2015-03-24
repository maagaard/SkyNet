PLANNER.md


# Planner

## Server defined agent actions 
- Move(agent-direction)
- Push(agent-move-direction, box-push-direction)
- Pull(agent-move-direction, current-direction)
- NoOp 

### Directions
- N, W, E, S


## Approach
- Start with goals
- Choose one goal
- Find literals that fulfill goal
- Perform actions that achieve literals


### POP approach
- Heuristics
- Define goals, actions, commands, literals

- Loop
    - Choose open precondition to goal state
    - Find actions that achieve condition
- Check if actions break established conditions or is blocked by conditions
    - Add ordering constraint ordering B to come before A 
- Add preconditions to set of conditions (in a sub-plan) 
- 


<!-- #### Goal prioritizing
- Choose goal
- Loop
    - Find open precondition
    - Find action that achieves condition
    - return "plan" for achieving goal
- Check if other goals conflict with plan
- Prioritize goals with regards to conflicts -->


## Planner knowledge
- Goals
- Priorities
- Plan
- Map(.lvl) input --> plan output (sequence of action)
- Fully observable and deterministic environment


### 1st iteration
- Single agent handling

### 2nd iteration
- Multi agent handling 