import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FinancialsService } from '../../services/financials.service';
import { RoleCostConfig } from '../../financials.model';

@Component({
  selector: 'app-role-rates',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './role-rates.component.html',
  styleUrl: './role-rates.component.scss'
})
export class RoleRatesComponent implements OnInit {
  rates: RoleCostConfig[] = [];
  editingRole: string | null = null;
  editBuffer: Partial<RoleCostConfig> = {};
  showAddForm = false;
  newRate: RoleCostConfig = { role: '', costRate: 0, marginRate: 0, hoursPerWeek: 40, totalHours: null };
  saveError = '';

  constructor(private financialsService: FinancialsService) {}

  ngOnInit(): void { this.loadRates(); }

  loadRates(): void {
    this.financialsService.getRoleCostConfigs().subscribe({
      next: (data) => this.rates = data.sort((a, b) => a.role.localeCompare(b.role))
    });
  }

  startEdit(rate: RoleCostConfig): void {
    this.editingRole = rate.role;
    this.editBuffer = { ...rate };
  }

  cancelEdit(): void { this.editingRole = null; this.editBuffer = {}; }

  saveEdit(): void {
    if (!this.editBuffer.costRate || !this.editBuffer.hoursPerWeek) return;
    this.financialsService.saveRoleCostConfig({
      role: this.editingRole!,
      costRate: Number(this.editBuffer.costRate),
      marginRate: Number(this.editBuffer.marginRate ?? 0),
      hoursPerWeek: Number(this.editBuffer.hoursPerWeek),
      totalHours: this.editBuffer.totalHours != null ? Number(this.editBuffer.totalHours) : null
    }).subscribe({ next: () => { this.loadRates(); this.cancelEdit(); } });
  }

  saveNew(): void {
    this.saveError = '';
    if (!this.newRate.role.trim())                            { this.saveError = 'Role is required'; return; }
    if (!this.newRate.costRate || this.newRate.costRate <= 0) { this.saveError = 'Cost rate must be > 0'; return; }
    if (!this.newRate.hoursPerWeek || this.newRate.hoursPerWeek <= 0) { this.saveError = 'Hours/week must be > 0'; return; }

    this.financialsService.saveRoleCostConfig(this.newRate).subscribe({
      next: () => {
        this.loadRates();
        this.showAddForm = false;
        this.newRate = { role: '', costRate: 0, marginRate: 0, hoursPerWeek: 40, totalHours: null };
      }
    });
  }
}
