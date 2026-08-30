import { Component, forwardRef, input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

export interface SelectOption<T = string> {
  value: T;
  label: string;
}

let nextId = 0;

@Component({
  selector: 'app-select',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectComponent),
      multi: true,
    },
  ],
  template: `
    <div class="flex flex-col gap-1">
      @if (label()) {
        <label [for]="id" class="text-sm font-medium text-neutral-700">{{ label() }}</label>
      }
      <select
        [id]="id"
        [disabled]="disabled"
        [value]="value"
        (change)="onSelect($event)"
        (blur)="onTouched()"
        class="rounded-md border border-neutral-300 bg-white px-3 py-2 text-sm text-neutral-900 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:cursor-not-allowed disabled:bg-neutral-50"
      >
        @if (placeholder()) {
          <option value="" disabled selected>{{ placeholder() }}</option>
        }
        @for (option of options(); track option.value) {
          <option [value]="option.value">{{ option.label }}</option>
        }
      </select>
    </div>
  `,
})
export class SelectComponent implements ControlValueAccessor {
  readonly label = input<string | null>(null);
  readonly placeholder = input<string | null>(null);
  readonly options = input.required<SelectOption[]>();

  readonly id = `app-select-${nextId++}`;
  protected value = '';
  protected disabled = false;

  private onChange: (value: string) => void = () => {};
  protected onTouched: () => void = () => {};

  writeValue(value: string): void {
    this.value = value ?? '';
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  protected onSelect(event: Event): void {
    this.value = (event.target as HTMLSelectElement).value;
    this.onChange(this.value);
  }
}
