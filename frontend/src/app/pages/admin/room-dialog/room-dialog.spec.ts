import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomDialog } from './room-dialog';

describe('RoomDialog', () => {
  let component: RoomDialog;
  let fixture: ComponentFixture<RoomDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
