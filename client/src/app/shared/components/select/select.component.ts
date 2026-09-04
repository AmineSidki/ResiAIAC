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
        <label [for]="id" class="text-sm font-medium text-neutral-700 dark:text-neutral-300">{{ label() }}</label>
      }
      <select
        [id]="id"
        [disabled]="disabled"
        [value]="value"
        (change)="onSelect($event)"
        (blur)="onTouched()"
        class="rounded-md border border-neutral-300 bg-white px-3 py-2 text-sm text-neutral-900 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:cursor-not-allowed disabled:bg-neutral-50 dark:border-white/10 dark:bg-white/5 dark:text-neutral-100 dark:disabled:bg-white/5"
      >
        @if (placeholder()) {
          <!--
            "clearable" selects (filter bars) leave the placeholder option
            enabled and selectable, so picking it back resets the filter —
            previously it was always disabled, which meant a table filter
            could be set but never cleared back through the dropdown itself.
            Form selects (required fields) keep the old disabled behavior.
          -->
          <option value="" [disabled]="!clearable()">{{ placeholder() }}</option>
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
  /** When true, the placeholder option stays selectable — used by table filter bars so users can clear back to "all". */
  readonly clearable = input(false);

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
