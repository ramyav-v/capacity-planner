// GET /dashboard
export interface DashboardResponse {
  metrics: DashboardMetrics;
  headcount: HeadcountItem[];
  allocationByRole: RoleDistribution[];
  clusterDistribution: ClusterDistribution[];
  utilizationStatus: UtilizationStatus;
  resourceAllocationByRole: ResourceAllocationByRole[];
}

export interface DashboardMetrics {
  totalEmployees: number;
  avgUtilization: number;
  activeProjects: number;
  overUtilization: number;
  underUtilization: number;
}

export interface HeadcountItem {
  role: string;
  zinier: number;
  nonZinier: number;
  total: number;
}

export interface RoleDistribution {
  role: string;
  count: number;
  names: string[];
}

export interface ClusterDistribution {
  region: string;
  count: number;
  names: string[];
}

export interface UtilizationStatus {
  optimal: number;
  overUtilized: number;
  underUtilized: number;
  optimalNames: string[];
  overUtilizedNames: string[];
  underUtilizedNames: string[];
}

export interface ResourceAllocationByRole {
  role: string;
  allocated: number;
  availableSeats: number;
  percentage: number;
  availableNames: string[];
}

// GET /dashboard/allocations
export interface ResourceAllocationResponse {
  quarter: string;
  viewType: 'WEEK' | 'QUARTER';
  weekStartDate: string | null;
  resources: ResourceAllocationDetail[];
}

export interface ResourceAllocationDetail {
  employeeId: number;
  name: string;
  role: string;
  region: string;
  utilization: number;
  status: string;
}

// GET /dashboard/projects
export interface ProjectSummary {
  id: number;
  name: string;
  code: string;
}

// GET /dashboard/projects/{id}/allocations
export interface ProjectAllocationResponse {
  projectId: number;
  projectName: string;
  projectCode: string;
  quarter: string;
  viewType: 'WEEK' | 'QUARTER';
  weekStartDate: string | null;
  resources: ResourceAllocationDetail[];
}
