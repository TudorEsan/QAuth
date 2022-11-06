export interface IReservation {
  id: string;
  roomId: string;
  from: Date;
  durationMinutes: int;
  subject: string;
  guests: string[];
}

export interface IReservationForm {
  roomId: string;
  from: Date;
  durationMinutes: int;
  subject: string;
  guests: string[];
}
