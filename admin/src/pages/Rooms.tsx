import { AccountTreeSharp, DeleteOutline, Refresh } from "@mui/icons-material";
import { LoadingButton } from "@mui/lab";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  Typography,
} from "@mui/material";
import { DataGrid, GridColumns } from "@mui/x-data-grid";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { number } from "yup/lib/locale";
import { ControlledTextField } from "../components";
import { useAccounts } from "../hooks/useAccounts";
import { useRooms } from "../hooks/useRooms";
import { deleteUser, registerUser } from "../service/AuthService";
import { addRoom, deleteRoom } from "../service/RoomsService";
import { IAccount, IAccountForm } from "../types/account";

const RoomsDataGrid = ({ open }: { open: boolean }) => {
  const { rooms, refresh } = useRooms();

  useEffect(() => {
    if (open === false) {
      refresh();
    }
  }, [open]);

  const handleDelete = async (id: string) => {
    try {
      await deleteRoom(id);
      refresh();
    } catch (e) {
      alert(e);
    }
  };

  const columns = [
    { field: "id", headerName: "Id", flex: 1 },
    { field: "name", headerName: "Name", flex: 1 },
    { field: "minimalRole", headerName: "Minimum Role", flex: 1 },
    {
      field: "actions",
      headerName: "actions",
      flex: 1,
      align: "center",
      renderCell: (r: any) => {
        return (
          <>
            <IconButton onClick={() => handleDelete(r.id)}>
              <DeleteOutline />
            </IconButton>
          </>
        );
      },
    },
  ] as GridColumns<IRoom>;

  return (
    <DataGrid
      rows={rooms.data || []}
      columns={columns}
      loading={rooms.loading}
      getRowId={(row) => row.id}
      autoHeight
      disableSelectionOnClick
    />
  );
};

interface ICreateDialog {
  open: boolean;
  setOpen: (x: boolean) => void;
}

export const CreateRoomDialog = ({ open, setOpen }: ICreateDialog) => {
  const { handleSubmit, control, reset } = useForm<IRoomForm>();
  const [loading, setLoading] = useState(false);

  const onAdd = async (data: IRoomForm) => {
    setLoading(true);
    try {
      data.minimalRole = Number(data.minimalRole);
      console.log(data);
      await addRoom(data);
      setOpen(false);
    } catch (e) {
      alert(e);
    }
    setLoading(false);
  };

  return (
    <Dialog open={open} onClose={() => setOpen(false)}>
      <DialogTitle>Create Account</DialogTitle>
      <DialogContent sx={{ minWidth: "350px", p: 3 }}>
        <Grid container gap={2}>
          <Grid xs={12}>
            <ControlledTextField
              control={control}
              name="name"
              label="Room Name"
            />
          </Grid>
          <Grid xs={12}>
            <ControlledTextField
              control={control}
              type="number"
              defaultValue={0}
              name="minimalRole"
              label="Minimal Role"
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions sx={{ pl: 3, pr: 3, pb: 2 }}>
        <LoadingButton
          loading={loading}
          variant="contained"
          onClick={handleSubmit(onAdd)}
        >
          Add
        </LoadingButton>
        <Button variant="outlined" onClick={() => setOpen(false)}>
          Discard
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export const Rooms = () => {
  const [open, setOpen] = useState(false);

  return (
    <Grid container spacing={2}>
      <CreateRoomDialog open={open} setOpen={setOpen} />
      <Grid
        item
        justifyContent="space-between"
        alignItems="center"
        container
        xs={12}
      >
        <Grid>
          <Typography variant="h4">Rooms</Typography>
        </Grid>
        <Grid>
          <Button onClick={() => setOpen(true)} variant="contained">
            Add Room
          </Button>
        </Grid>
      </Grid>
      <Grid item xs={12}>
        <RoomsDataGrid open={open} />
      </Grid>
    </Grid>
  );
};
