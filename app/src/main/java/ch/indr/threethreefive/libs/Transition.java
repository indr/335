/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

public class Transition<T> {
  private final T from;
  private final T to;

  protected Transition(T from, T to) {
    this.from = from;
    this.to = to;
  }

  public static <T> Transition<T> create() {
    return new Transition<T>((T) null, (T) null);
  }

  public T getFrom() {
    return from;
  }

  public T getTo() {
    return to;
  }

  public Transition<T> next(T value) {
    return new Transition<T>(this.to, value);
  }

  @Override public String toString() {
    return "Transition{" +
        "from=" + from +
        ", to=" + to +
        '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Transition<?> that = (Transition<?>) o;

    if (from != null ? !from.equals(that.from) : that.from != null) return false;
    return to != null ? to.equals(that.to) : that.to == null;

  }

  @Override public int hashCode() {
    int result = from != null ? from.hashCode() : 0;
    result = 31 * result + (to != null ? to.hashCode() : 0);
    return result;
  }
}

