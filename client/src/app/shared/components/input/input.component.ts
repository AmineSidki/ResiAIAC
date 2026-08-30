import { Component, forwardRef, input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

let nextId = 0;

@Component({
  selector: 'app-input',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputComponent),
      multi: true,
    },
  ],
  template: `
    <div class="flex flex-col gap-1">
      @if (label()) {
        <label [for]="id" class="text-sm font-medium text-neutral-700">{{ label() }}</label>
      }
      <input
        [id]="id"
        [type]="type()"
        [placeholder]="placeholder()"
        [disabled]="disabled"
        [value]="value"
        (input)="onInput($event)"
        (blur)="onTouched()"
        class="rounded-md border px-3 py-2 text-sm text-neutral-900 shadow-sm placeholder:text-neutral-400 focus:outline-none focus:ring-2 disabled:cursor-not-allowed disabled:bg-neutral-50"
        [class.border-neutral-300]="!errorText()"
        [class.focus:ring-primary-500]="!errorText()"
        [class.focus:border-primary-500]="!errorText()"
        [class.border-danger-500]="!!errorText()"
        [class.focus:ring-danger-500]="!!errorText()"
      />
      @if (errorText()) {
        <p class="text-xs text-danger-500">{{ errorText() }}</p>
      } @else if (hint()) {
        <p class="text-xs text-neutral-500">{{ hint() }}</p>
      }
    </div>
  `,
})
export class InputComponent implements ControlValueAccessor {
  readonly label = input<string | null>(null);
  readonly type = input<'text' | 'email' | 'password' | 'number' | 'tel'>('text');
  readonly placeholder = input('');
  readonly hint = input<string | null>(null);
  readonly errorText = input<string | null>(null);

  readonly id = `app-input-${nextId++}`;
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

  protected onInput(event: Event): void {
    this.value = (event.target as HTMLInputElement).value;
    this.onChange(this.value);
  }
}
