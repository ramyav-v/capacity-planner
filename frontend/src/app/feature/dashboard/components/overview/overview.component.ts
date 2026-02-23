import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgApexchartsModule } from 'ng-apexcharts';
import {
  ApexChart,
  ApexPlotOptions,
  ApexDataLabels,
  ApexXAxis,
  ApexYAxis,
  ApexLegend,
  ApexFill,
  ApexStroke,
  ApexTooltip,
  ApexResponsive,
  ApexNonAxisChartSeries,
  ApexAxisChartSeries
} from 'ng-apexcharts';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardResponse } from '../../dashboard.model';

export interface KpiCard {
  title: string;
  value: string;
  iconBg: string;
  icon: string;
}

export interface RoleAllocation {
  role: string;
  icon: string;
  iconBg: string;
  allocated: number;
  total: number;
  percent: number;
  availablePeople: number;
}

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss'
})
export class OverviewComponent implements OnInit {

  dataLoaded = false;
  kpiCards: KpiCard[] = [];

  // ========== Headcount Bar Chart ==========
  totalFullTime = 0;
  totalContractors = 0;

  barChartSeries: ApexAxisChartSeries = [
    { name: 'Zinier', data: [] },
    { name: 'Non-Zinier', data: [] }
  ];

  barChartOptions: ApexChart = {
    type: 'bar',
    height: 260,
    toolbar: { show: false },
    fontFamily: "'Open Sans', sans-serif"
  };

  barPlotOptions: ApexPlotOptions = {
    bar: {
      horizontal: true,
      barHeight: '55%',
      borderRadius: 4
    }
  };

  barXAxis: ApexXAxis = {
    categories: [],
    labels: { style: { fontSize: '13px', colors: '#171b1f' } },
    axisBorder: { show: false },
    axisTicks: { show: false }
  };

  barYAxis: ApexYAxis = {
    labels: { style: { fontSize: '13px', colors: '#171b1f', fontWeight: '600' } }
  };

  barDataLabels: ApexDataLabels = {
    enabled: true,
    style: { fontSize: '11px', fontWeight: '600', colors: ['#fff'] },
    formatter: (val: number) => val > 0 ? val.toString() : ''
  };

  barColors = ['#2e2791ff', '#aaa5e0ff'];

  barLegend: ApexLegend = {
    position: 'bottom',
    horizontalAlign: 'center',  
    fontSize: '12px',
    fontWeight: '600',
    markers: { shape: 'circle' as any },
    labels: { colors: '#171b1f' },
    itemMargin: { horizontal: 16 }
  };

  barTooltip: ApexTooltip = {
    theme: 'light',
    y: { formatter: (val: number) => val + ' people' }
  };

  barStroke: ApexStroke = { show: false };

  barGrid = {
    borderColor: '#f0f0f0',
    xaxis: { lines: { show: true } },
    yaxis: { lines: { show: false } }
  };

  // ========== Donut Charts ==========
  private donutChartBase: ApexChart = {
    type: 'donut',
    height: 230,
    fontFamily: "'Open Sans', sans-serif"
  };

  private donutPlotBase: ApexPlotOptions = {
    pie: { donut: { size: '58%' } }
  };

  private donutDataLabelsOff: ApexDataLabels = { enabled: false };
  private donutStroke: ApexStroke = { show: false };

  // Allocation by Roles
  allocationSeries: ApexNonAxisChartSeries = [];
  allocationChart: ApexChart = { ...this.donutChartBase };
  allocationLabels: string[] = [];
  allocationColors = ['#5c6bc0', '#ab47bc', '#26a69a', '#ffa726', '#78909c'];
  allocationPlot: ApexPlotOptions = { ...this.donutPlotBase };
  allocationDataLabels: ApexDataLabels = { ...this.donutDataLabelsOff };
  allocationStroke: ApexStroke = { ...this.donutStroke };
  allocationLegend: ApexLegend = {
    position: 'bottom',
    fontSize: '10px',
    markers: { shape: 'circle' as any },
    labels: { colors: '#171b1f' },
    itemMargin: { horizontal: 4, vertical: 2 },
    formatter: (label: string, opts: any) =>
      `${label} ${opts.w.config.series[opts.seriesIndex]}`
  };
  allocationTooltip: ApexTooltip = { y: { formatter: (v: number) => v + ' people' } };

  // Cluster Distribution
  clusterSeries: ApexNonAxisChartSeries = [];
  clusterChart: ApexChart = { ...this.donutChartBase };
  clusterLabels: string[] = [];
  clusterColors = ['#5c6bc0', '#ffa726', '#26a69a', '#ef5350'];
  clusterPlot: ApexPlotOptions = { ...this.donutPlotBase };
  clusterDataLabels: ApexDataLabels = { ...this.donutDataLabelsOff };
  clusterStroke: ApexStroke = { ...this.donutStroke };
  clusterLegend: ApexLegend = {
    position: 'bottom',
    fontSize: '10px',
    markers: { shape: 'circle' as any },
    labels: { colors: '#171b1f' },
    itemMargin: { horizontal: 4, vertical: 2 },
    formatter: (label: string, opts: any) =>
      `${label} ${opts.w.config.series[opts.seriesIndex]}`
  };
  clusterTooltip: ApexTooltip = { y: { formatter: (v: number) => v + ' people' } };

  // Utilization Status
  utilizationSeries: ApexNonAxisChartSeries = [];
  utilizationChart: ApexChart = { ...this.donutChartBase };
  utilizationLabels = ['Optimal', 'Over Util.', 'Under Util.'];
  utilizationColors = ['#26a69a', '#ef5350', '#ffa726'];
  utilizationPlot: ApexPlotOptions = { ...this.donutPlotBase };
  utilizationDataLabels: ApexDataLabels = { ...this.donutDataLabelsOff };
  utilizationStroke: ApexStroke = { ...this.donutStroke };
  utilizationLegend: ApexLegend = {
    position: 'bottom',
    fontSize: '10px',
    markers: { shape: 'circle' as any },
    labels: { colors: '#171b1f' },
    itemMargin: { horizontal: 4, vertical: 2 },
    formatter: (label: string, opts: any) =>
      `${label} ${opts.w.config.series[opts.seriesIndex]}`
  };
  utilizationTooltip: ApexTooltip = { y: { formatter: (v: number) => v + ' people' } };

  // ========== Role Allocation Cards ==========
  private readonly roleIcons: Record<string, { icon: string; iconBg: string }> = {
    'Dev':     { icon: 'M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z', iconBg: '#e3f2fd' },
    'QA':      { icon: 'M19.35 10.04A7.49 7.49 0 0012 4C9.11 4 6.6 5.64 5.35 8.04A5.99 5.99 0 000 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96z', iconBg: '#e8f5e9' },
    'TL':      { icon: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z', iconBg: '#fff3e0' },
    'PM':      { icon: 'M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z', iconBg: '#fce4ec' },
    'Support': { icon: 'M11.5 2C6.81 2 3 5.81 3 10.5S6.81 19 11.5 19h.5v3c4.86-2.34 8-7 8-11.5C20 5.81 16.19 2 11.5 2zm1 14.5h-2v-2h2v2zm0-3.5h-2c0-3.25 3-3 3-5 0-1.1-.9-2-2-2s-2 .9-2 2h-2c0-2.21 1.79-4 4-4s4 1.79 4 4c0 2.5-3 2.75-3 5z', iconBg: '#ede7f6' }
  };

  private readonly defaultRoleIcon = { icon: 'M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z', iconBg: '#f5f5f5' };

  roleAllocations: RoleAllocation[] = [];

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getDashboard().subscribe({
      next: (data) => this.populateDashboard(data),
      error: (err) => console.error('Failed to load dashboard', err)
    });
  }

  private populateDashboard(data: DashboardResponse): void {
    const m = data.metrics;

    // KPI Cards
    this.kpiCards = [
      {
        title: 'Total Resources',
        value: m.totalEmployees.toString(),
        iconBg: '#e3f2fd',
        icon: 'M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5s-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z'
      },
      {
        title: 'Avg Utilization',
        value: Math.round(m.avgUtilization * 100) + '%',
        iconBg: '#fff8e1',
        icon: 'M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z'
      },
      {
        title: 'Active Projects',
        value: m.activeProjects.toString(),
        iconBg: '#e8f5e9',
        icon: 'M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14zM7 12h2v5H7zm4-3h2v8h-2zm4-3h2v11h-2z'
      },
      {
        title: 'Overutilization',
        value: m.overUtilization.toString(),
        iconBg: '#fce4ec',
        icon: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z'
      },
      {
        title: 'Underutilization',
        value: m.underUtilization.toString(),
        iconBg: '#fff3e0',
        icon: 'M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z'
      }
    ];

    // Headcount bar chart
    const roles = data.headcount.map(h => h.role);
    const zinierData = data.headcount.map(h => h.zinier);
    const nonZinierData = data.headcount.map(h => h.nonZinier);

    this.barChartSeries = [
      { name: 'Zinier', data: zinierData },
      { name: 'Non-Zinier', data: nonZinierData }
    ];
    this.barXAxis = {
      ...this.barXAxis,
      categories: roles
    };
    this.totalFullTime = zinierData.reduce((a, b) => a + b, 0);
    this.totalContractors = nonZinierData.reduce((a, b) => a + b, 0);

    // Allocation by Roles donut
    this.allocationLabels = data.allocationByRole.map(r => r.role);
    this.allocationSeries = data.allocationByRole.map(r => r.count);

    // Cluster Distribution donut
    this.clusterLabels = data.clusterDistribution.map(c => c.region);
    this.clusterSeries = data.clusterDistribution.map(c => c.count);

    // Utilization Status donut
    const u = data.utilizationStatus;
    this.utilizationSeries = [u.optimal, u.overUtilized, u.underUtilized];

    // Resource Allocation by Role cards
    this.roleAllocations = data.resourceAllocationByRole.map(r => {
      const total = r.allocated + r.availableSeats;
      const pct = total > 0 ? Math.round((r.allocated / total) * 100) : 0;
      const iconData = this.roleIcons[r.role] || this.defaultRoleIcon;
      return {
        role: r.role,
        icon: iconData.icon,
        iconBg: iconData.iconBg,
        allocated: r.allocated,
        total: total,
        percent: pct,
        availablePeople: r.availableSeats
      };
    });

    this.dataLoaded = true;
  }

  getPercentBarColor(percent: number): string {
    if (percent >= 100) return '#ef5350';
    if (percent >= 80) return '#ffa726';
    return '#2e2791';
  }
}
