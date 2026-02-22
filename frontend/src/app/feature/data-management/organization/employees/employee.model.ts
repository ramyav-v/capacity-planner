export type EmployeeRole = 'PM' | 'TL' | 'Dev' | 'QA' | 'Support';
export type EmployeeCompany = 'Zinier' | 'Sankey' | 'TopGrep';
export type EmployeeRegion = 'US' | 'EMEA' | 'AU';

export interface Employee {
  id: number | null;
  name: string;
  email: string;
  role: EmployeeRole;
  company: EmployeeCompany;
  region: EmployeeRegion;
  startDate: string;
  endDate: string | null;
  capacityFactor: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface EmployeeDto {
  name: string;
  email: string;
  role: EmployeeRole;
  company: EmployeeCompany;
  region: EmployeeRegion;
  startDate: string;
  endDate: string | null;
  capacityFactor: number;
}
