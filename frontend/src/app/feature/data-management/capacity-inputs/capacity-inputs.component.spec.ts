import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CapacityInputsComponent } from './capacity-inputs.component';

describe('CapacityInputsComponent', () => {
  let component: CapacityInputsComponent;
  let fixture: ComponentFixture<CapacityInputsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CapacityInputsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CapacityInputsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
