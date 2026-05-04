export interface RoleCostConfig {
  role: string;
  costRate: number;
  marginRate: number | null;
  hoursPerWeek: number;
  totalHours: number | null;
}

export interface ProjectResourcePlan {
  id?: number;
  projectId: number;
  role: string;
  totalHours: number;
}

export interface ProjectFinancialRole {
  role: string;
  totalHours: number;
  rate: number;
  forecastedCost: number;
}

export interface ProjectFinancial {
  projectId: number;
  projectName: string;
  projectCode: string;
  psFee: number | null;
  forecastedCostNonDisc: number;
  marginNonDisc: number | null;
  marginPctNonDisc: number | null;
  forecastedCostDisc: number;
  marginDisc: number | null;
  marginPctDisc: number | null;
  nonDiscBreakdown?: ProjectFinancialRole[];
  discBreakdown?: ProjectFinancialRole[];
  resourcePlan?: ProjectResourcePlan[];
}
