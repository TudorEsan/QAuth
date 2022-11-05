export const handleSuccess = (data: any, setState: (data: any) => void) => {
  setState({
    data,
    loading: false,
    error: null,
  });
};

export const handleError = (setState: (data: any) => void, error: string) => {
  setState({
    data: null,
    loading: false,
    error,
  });
};
