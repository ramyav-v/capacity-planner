export interface AllocationWeek {
  id: number | null;
  projectId?: string;
  employeeId?: string;
  weekStartDate: string;
  allocationPct: number;
}

export interface AllocationGroup {
  employeeId: number;
  employeeName: string | null;
  role: string | null;
  region: string | null;
  projectId: number;
  allocations: AllocationWeek[];
}

export interface AllocationRow {
  id: number | null;
  employeeId: number;
  employeeName: string;
  projectId: number;
  projectName: string;
  weekStartDate: string;
  allocationPct: number;
}

export interface AllocationDto {
  employeeId: number;
  projectId: number;
  weekStartDate: string;
  allocationPct: number;
}

export interface QuarterAllocationRequest {
  employeeId: number;
  projectId: number;
  allocations: { weekStartDate: string; allocationPct: number }[];
}
