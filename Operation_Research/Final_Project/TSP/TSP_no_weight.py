from gurobipy import *
import pandas as pd

def TSP_no_weight():
    distance_matrix = pd.read_csv('/Users/jasper/Operation Research/Final_Project/distance_matrix.csv')

    nodes = []
    for i in range(len(distance_matrix)):
        nodes.append(str(i+1))
    distance = {}
    for i in range(distance_matrix.shape[0]):
        for j in range(distance_matrix.shape[1]):
            distance[(str(i+1),str(j+1))] = distance_matrix.iloc[i,j]
    arcs, distance = multidict(distance)
    # Create optimization model
    m = Model('TSP')
    # Create variables
    x = {}
    u = {}
    for i,j in arcs:
        x[i,j] = m.addVar(obj=distance[i,j], vtype = 'B',
        name='x_%s%s' % (i, j))
    N = len(nodes)
    for i in nodes:
        if i != nodes[N-1]:
            u[i] = m.addVar(obj=0, name='u_%s' % i)
    m.update()
    # Constraint for sum of incoming links to j
    for j in nodes:
        m.addConstr(quicksum(x[i,j]
                for i in nodes if i != j) == 1,
                        'incom_%s' % (j))
    # Constraint for sum of outgoing links from i
    for i in nodes:
        m.addConstr(quicksum(x[i,j]
                for j in nodes if i != j) == 1,
                        'outgo_%s' % (i))
    # Subtour elimination constraints
    for i,j in arcs:
        if i != nodes[N-1] and j != nodes[N-1]:
            m.addConstr(u[i] - u[j] + N*x[i,j] <= N-1,
                    'subtour_%s_%s' % (i, j))
    # Compute optimal solution
    m.optimize()
    # Print solution
    route = []
    if m.status == GRB.Status.OPTIMAL:
        print('objective: %f' % m.ObjVal)
        solution = m.getAttr('x', x)
        for i,j in arcs:
            if solution[i,j] > 0:
                    print('%s -> %s: %g' % (i, j, solution[i,j]))
                    route.append([i,j])
    print(route)
    route = pd.DataFrame(route)
    route.to_csv('/Users/jasper/Operation Research/Final_Project/route_no_weight.csv', index=False, header=False)